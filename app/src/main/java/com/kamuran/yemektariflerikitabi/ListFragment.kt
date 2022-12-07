package com.kamuran.yemektariflerikitabi

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list.*


class ListFragment : Fragment() {

    var yemekIsmiListesi= ArrayList<String>()
    var yemekIdListesi= ArrayList<Int>()
    private lateinit var listeAdapter:ListeRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeAdapter= ListeRecyclerAdapter(yemekIsmiListesi,yemekIdListesi)
        recycleView.layoutManager= LinearLayoutManager(context)
        recycleView.adapter= listeAdapter

        sqlveriAlma()
    }
    fun sqlveriAlma(){
        try {
          activity?.let {
              val database= it.openOrCreateDatabase("yemekler",Context.MODE_PRIVATE,null)
              var cursor= database.rawQuery("SELECT*FROM yemekler",null)
              val yemekIsmiIndex= cursor.getColumnIndex("yemekismi")
              val yemekIdIndext= cursor.getColumnIndex("id")

              yemekIsmiListesi.clear()
              yemekIdListesi.clear()
              while(cursor.moveToNext()){
                yemekIsmiListesi.add(cursor.getString(yemekIsmiIndex))
                  yemekIdListesi.add(cursor.getInt(yemekIdIndext))
              }
              listeAdapter.notifyDataSetChanged()

          }
        }catch (e:Exception){

        }
    }

}