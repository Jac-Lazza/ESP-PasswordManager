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

        //I simboli possibili sono suddivisi in gruppi di alfabeti
        const val ALPHABET_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        const val ALPHABET_LOWER = "abcdefghijklmnopqrstuvwxyz"
        const val ALPHABET_DIGIT = "0123456789"
        const val ALPHABET_SPECIAL = " !\"#\$%&'()*+,-./:;<=>?@[\\]^_`{|}~" //Lista dei caratteri speciali presa da OWASP

        //I valori tornano utili se utilizzati come percentuali
        const val PASSWORD_WEAK = 33
        const val PASSWORD_MEDIUM = 66
        const val PASSWORD_HARD = 100
    }

    /*
    * Va chiamato una volta per inizializzare la chiave di criptazione e il sale per l'hashing della
    * master password
    * */
    fun init(){
        /*
        * L'hash viene "salato" in modo da renderlo più sicuro contro attacchi di ricerca di hash noti,
        * basta solo che venga memorizzato
        * */
        val salt = generatePassword(5)
        val editor = preferences.edit()
        editor.putString(SALT_KEY, salt)
        editor.apply()

        //Specifiche per una chiave AES, modalità GCM senza padding
        val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keySpec = KeyGenParameterSpec.Builder(SECRET_KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()

        //Generazione della chiave all'interno del KeyStore
        keyGen.init(keySpec)
        keyGen.generateKey()


    }

    fun hash(password : String) : String {
        val salt = getSalt()
        val saltedPassword = "$salt:$password"
        val passwordBytes : ByteArray = saltedPassword.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(passwordBytes).toString(Charsets.UTF_8)
    }

    fun encrypt(clearText : String) : String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        val clearTextBytes = clearText.toByteArray(Charsets.UTF_8)
        val iv = cipher.iv //Inizializzato in automatico dalla classe Cipher in modo casuale

        val encryptedBytes = cipher.doFinal(clearTextBytes)
        //L'IV non può essere perduto, viene concatenato alla stringa in uscita, la sua lunghezza è fissa
        return Base64.getEncoder().encodeToString(encryptedBytes + iv)
    }

    fun decrypt(cipherText : String) : String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val cipherTextBytes = Base64.getDecoder().decode(cipherText)
        val passwordEncryptedBytes = cipherTextBytes.sliceArray(0..(cipherTextBytes.size - 12 - 1))
        val iv = cipherTextBytes.sliceArray((cipherTextBytes.size - 12)..(cipherTextBytes.size - 1))
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)

        return cipher.doFinal(passwordEncryptedBytes).toString(Charsets.UTF_8)
    }

    /*
    * In base al valore dell'entropia della password, restituisce la sua forza.
    * L'idea è quella di rendere più consapevole l'utente nella scelta della sua master password
    * */
    fun strength(password : String) : Int{
        val strengthThreshold = 30.0
        val entropy = entropy(password)

        if(entropy <= strengthThreshold){
            return PASSWORD_WEAK
        }
        else if(entropy <= strengthThreshold * 3){
            return PASSWORD_MEDIUM
        }
        else if(entropy > strengthThreshold * 3){
            return PASSWORD_HARD
        }
        else{
            return 0
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
        if(keyStoreEntry == null){
            throw Exception("Cryptography configuration error: secret key not defined")
        }
        return keyStoreEntry.secretKey
    }

    /*
    * Calcola l'entropia di una password.
    * Questa funzione viene utilizzata nel calcolo della forza di una password
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
    * Se la stringa utilizza caratteri dell'alfabeto, restituisce la cardinalità dell'alfabeto,
    * altrimenti restituisce zero.
    * Questa funzione viene utilizzata nel calcolo dell'entropia
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