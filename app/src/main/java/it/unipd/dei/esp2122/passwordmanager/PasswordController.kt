package it.unipd.dei.esp2122.passwordmanager

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.math.log
import kotlin.random.Random

class PasswordController(private val preferences : SharedPreferences){

    companion object{
        private const val SALT_KEY = "PASSWORD_CONTROLLER_SALT_KEY"
        private const val SECRET_KEY_ALIAS = "SECRET_KEY_ALIAS"

        //ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~
        const val ALPHABET_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        const val ALPHABET_LOWER = "abcdefghijklmnopqrstuvwxyz"
        const val ALPHABET_DIGIT = "0123456789"
        const val ALPHABET_SPECIAL = " !\"#\$%&'()*+,-./:;<=>?@[\\]^_`{|}~"

        //Let's think if it's to keep
        const val PASSWORD_WEAK = 33
        const val PASSWORD_MEDIUM = 66
        const val PASSWORD_HARD = 100
    }

    /*
    * Must be called once to initialize the salt and cryptographic key to use in this vault
    * It's called once and then the data is saved in the preferences
    * */
    fun init(){
        //Generating the salt and saving it
        val salt = generatePassword(5) //The salt can be considered as a small additional password
        val editor = preferences.edit()
        editor.putString(SALT_KEY, salt)
        editor.apply()

        //Preparing the specifications for an AES key, Galois Counter Mode with no padding
        val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keySpec = KeyGenParameterSpec.Builder(SECRET_KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()

        //Generating the key inside the keystore (has it's own small CPU and random number generator)
        keyGen.init(keySpec)
        keyGen.generateKey()


    }

    fun hash(password : String) : String {
        val salt = getSalt()
        val saltedPassword = "$salt:$password"
        val passwordBytes : ByteArray = saltedPassword.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(passwordBytes).toString(Charsets.UTF_8)
        println("Hash: $hash") //DEBUG
        return hash
    }

    fun encrypt(clearText : String) : String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        //Need to check if the input needs padding
        val clearTextBytes = clearText.toByteArray(Charsets.UTF_8)
        val iv = cipher.iv //This gets a random initialization vector every time

        val encryptedBytes = cipher.doFinal(clearTextBytes)
        //We need to return a string, but the iv can't be lost
        val result = Base64.getEncoder().encodeToString(encryptedBytes + iv)

        return result
    }

    fun decrypt(cipherText : String) : String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val cipherTextBytes = Base64.getDecoder().decode(cipherText)
        val passwordEncryptedBytes = cipherTextBytes.sliceArray(0..(cipherTextBytes.size - 12 - 1))
        val iv = cipherTextBytes.sliceArray((cipherTextBytes.size - 12)..(cipherTextBytes.size - 1))
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)

        val result = cipher.doFinal(passwordEncryptedBytes).toString(Charsets.UTF_8)

        return result
    }

    fun strength(password : String) : Int{
        val strengthThreshold = 30.0
        val entropy = entropy(password)

        if(entropy <= strengthThreshold){
            return PASSWORD_WEAK
        }
        else if(entropy <= strengthThreshold * 3){
            return PASSWORD_MEDIUM
        }
        else{
            return PASSWORD_HARD
        }
    }

    fun generatePassword(length : Int = 16) : String {
        if(length <= 0){
            throw IllegalArgumentException("Length can't be negative or zero!")
        }
        val alphabet : String = ALPHABET_UPPER + ALPHABET_LOWER + ALPHABET_DIGIT + ALPHABET_SPECIAL
        var password = ""
        for(i in 1..length){
            password += alphabet[Random.nextInt(alphabet.length)]
        }
        return password
    }

    private fun getSalt() : String {
        val salt = preferences.getString(SALT_KEY, null)
        if(salt == null){
            throw Exception("Cryptography configuration error: salt is null")
        }
        return salt
    }

    private fun getKey() : SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        val keyStoreEntry = keyStore.getEntry(SECRET_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        //To check
        if(keyStoreEntry == null){
            throw Exception("Cryptography configuration error: secret key not defined")
        }
        //End of check
        return keyStoreEntry.secretKey
    }

    /*
    * Returns the entropy of a password.
    * The entropy is calculated as the length of the password multiplied by the logarithm in base 2
    * of the cardinality of the alphabet that the password uses.
    * The alphabet is divided in 4 categories, a strong password should use character from all of them
    * */
    private fun entropy(password : String) : Double {
        var cardinality = 0
        cardinality += alphabetCardinality(password, ALPHABET_UPPER)
        cardinality += alphabetCardinality(password, ALPHABET_LOWER)
        cardinality += alphabetCardinality(password, ALPHABET_DIGIT)
        cardinality += alphabetCardinality(password, ALPHABET_SPECIAL)
        return password.length * log(cardinality.toDouble(), 2.0)
    }

    /*
    * If the string uses characters of the given alphabet, returns the cardinality of the alphabet,
    * zero otherwise.
    * This is used for calculating the entropy of a password
    * */
    private fun alphabetCardinality(password : String, alphabet : String) : Int{
        for(i in password){
            if(i in alphabet){
                return alphabet.length
            }
        }
        return 0
    }
}