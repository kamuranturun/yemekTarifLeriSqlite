package com.kamuran.yemektariflerikitabi

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_tarif.*
import java.io.ByteArrayOutputStream


class TarifFragment : Fragment() {
    var secilenGorsel: Uri?=null
    var secilenBitMap: Bitmap?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tarif, container, false)

            }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button.setOnClickListener{
            kaydet(it)
        }

        imageView.setOnClickListener {
            gorselSec(it)
        }

        arguments?.let {
            var gelenBilgi=TarifFragmentArgs.fromBundle(it).bilgi
            if(gelenBilgi.equals("menudengeldim")){
                //yeni ekle
                yemekIsmiText.setText("")
                yemekMalzemeText.setText("")
                button.visibility= View.VISIBLE//görünür

                val gorselSecArkaplani= BitmapFactory.decodeResource(context?.resources,R.drawable.secim)
                imageView.setImageBitmap(gorselSecArkaplani)
            }else{
                //önceki yemek

                button.visibility= View.INVISIBLE

                val secilenId= TarifFragmentArgs.fromBundle(it).id

                context?.let {
                    try {
                        val db= it.openOrCreateDatabase("Yemekler",Context.MODE_PRIVATE,null)
                        val cursor= db.rawQuery("SELECT*FROM yemekler WHERE id= ?",
                        arrayOf(secilenId.toString()))

                        val yemekIsmiIndex= cursor.getColumnIndex("yemekismi")
                        val yemekMalzemeIndex= cursor.getColumnIndex("yemekmalzemesi")
                        val yemekgorseli= cursor.getColumnIndex("gorsel")
                        while (cursor.moveToNext()){
                            yemekIsmiText.setText(cursor.getString(yemekIsmiIndex))
                            yemekMalzemeText.setText(cursor.getString(yemekMalzemeIndex))

                            val byteDizisi= cursor.getBlob(yemekgorseli)
                            val bitmap= BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size)
                            imageView.setImageBitmap(bitmap)
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }


    }

    fun kaydet(view:View) {
//sqlite kaydet
        var yemekIsmi= yemekIsmiText.text.toString()
        var yemekMalzemeleri=yemekMalzemeText.text.toString()

        if(secilenBitMap !=null){
            val kucukBitmap= kucukBitmapOlustur(secilenBitMap!!,300)

            val outPutStream= ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outPutStream)
            val byteDizisi= outPutStream.toByteArray()

            try {
                context?.let {
                    val database= it.openOrCreateDatabase("Yemekler", Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS yemekler" +
                            "(id INTEGER PRIMARY KEY,yemekismi VARCHAR, yemekmalzemesi VARCHAR, gorsel BLOB)")

                    val sqlString = "INSERT INTO yemekler (yemekismi, yemekmalzemesi, gorsel) VALUES (?, ?, ?)"
                    val stetement= database.compileStatement(sqlString)
                    stetement.bindString(1,yemekIsmi)
                    stetement.bindString(2,yemekMalzemeleri)
                    stetement.bindBlob(3,byteDizisi)
                    stetement.execute()

                }

            }catch (e:Exception){
                e.printStackTrace()
            }
            val action= TarifFragmentDirections.actionTarifFragmentToListFragment()
            Navigation.findNavController(view).navigate(action)
        }
     }

    fun gorselSec(view: View){
activity?.let {
    if(ContextCompat.checkSelfPermission(it.applicationContext,Manifest.permission
            .READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
        //izin verilmedi ise isteyeceğiz

        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)

    }else{
        //izin verilmiş galeriye git

        val galeriIntent= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galeriIntent,2)
    }
}


    }
//istenen iznin sonucları
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

    if (requestCode==1){
        if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            //izn aldık
            val galeriIntent= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntent,2)
        }
    }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode==2 && resultCode== Activity.RESULT_OK && data !=null){

            secilenGorsel=data.data

            try {
                context?.let {
                    if (secilenGorsel !=null) {
                        if(Build.VERSION.SDK_INT>=28){
                            val source=   ImageDecoder.createSource(it.contentResolver, secilenGorsel!!)
                            secilenBitMap=ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(secilenBitMap)
                        }else{
                            secilenBitMap= MediaStore.Images.Media.getBitmap(
                                it.contentResolver,secilenGorsel
                            )
                            imageView.setImageBitmap(secilenBitMap)

                        }

                    }
                }

            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun kucukBitmapOlustur(kullanicininSectigiBitmap:Bitmap,maximumBoyut:Int):Bitmap{

        var width= kullanicininSectigiBitmap.width
        var height= kullanicininSectigiBitmap.height

        val bitmapOrani:Double= width.toDouble()/height.toDouble()
        if (bitmapOrani>1){
            //görselimiz yatay
            width=maximumBoyut
            val kisaltilmisHeight=width/bitmapOrani
            height= kisaltilmisHeight.toInt()
        }else{
            //görselimiz dikey
            height= maximumBoyut
            val kisaltilmisWidth= height*bitmapOrani
            width= kisaltilmisWidth.toInt()
        }

        return Bitmap.createScaledBitmap(kullanicininSectigiBitmap,width,height,true)
    }

}