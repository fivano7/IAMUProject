package hr.algebra.iamu.handler

import android.content.Context
import hr.algebra.iamu.R
import hr.algebra.iamu.factory.createGetHttpUrlConnection
import java.io.File
import java.net.HttpURLConnection
import java.nio.file.Files
import java.nio.file.Paths

fun downloadImageAndStore(context: Context, url: String, fileName: String): String? {
    //https://lh3.googleusercontent.com/rKEAX0x9BlAUKs8bxeBHOkaJDMYy00tSbDrV_1kOgOVz3vGjtgT0LiB2O6cARfUjXar8zFn5FFJDpcG3nB1IhTpRAPJfA2M5YjA6vmQ
    //val ext = url.substring(url.lastIndexOf(".")) //.jpg
    val ext = context.getString(R.string.png)
    val file: File = createFile(context, fileName, ext)

    //sad imamo file i trebamo skinut sliku s interneta i ubacit je u njega
    try {
        val con: HttpURLConnection = createGetHttpUrlConnection(url)
        Files.copy(con.inputStream, Paths.get(file.toURI())) //promjenili smo sdk sa 23 na 26 u gradle
        return file.absolutePath
    } catch (e: Exception) {
        //Log.e("DOWNLOAD IMAGE", e.message,e)
    }
    return null
}

fun createFile(context: Context, fileName: String, ext: String): File {
    //preko contexta pitamo android u koji folder možemo zapisivati
    val directory = context.applicationContext.getExternalFilesDir(null)
    val file = File(directory, File.separator + fileName + ext)

    if (file.exists()) {
        file.delete()
    }

    return file
}