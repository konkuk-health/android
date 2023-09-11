package com.example.lsproject2.fragment

import android.content.Intent
import android.os.Bundle
import android.view.ContentInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lsproject2.R
import com.example.lsproject2.databinding.FragmentDetailBinding
import com.example.lsproject2.databinding.ItemDetailBinding
import com.example.lsproject2.model.ContentModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class DetailViewFragment : Fragment() {
    lateinit var binding : FragmentDetailBinding
    lateinit var firestore : FirebaseFirestore
    lateinit var uid : String
    lateinit var auth : FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail,container,false)
        uid = FirebaseAuth.getInstance().uid!!

                binding.detailviewRecyclerveiw.adapter = DetailviewRecyclerviewAdapter()
                binding.detailviewRecyclerveiw.layoutManager = LinearLayoutManager(activity)

        return binding.root
    }
    inner class DetailViewHolder(var binding : ItemDetailBinding) : RecyclerView.ViewHolder(binding.root)
    inner class DetailviewRecyclerviewAdapter : RecyclerView.Adapter<DetailViewHolder>(){

        var contentModels = arrayListOf<ContentModel>()
        var contentUidsList = arrayListOf<String>()
        init {
            //데이터를 계속 지켜보는 것을 Snapshot -> 리소스를 많이 사용 -> 서버 비용 증가 -> 비용줄이면서
            //한번만 데이터를 읽어 드리는것을 Get

            firestore.collection("images")
                .addSnapshotListener { value, error ->
                    for (item in value!!.documentChanges){
                        if(item.type == DocumentChange.Type.ADDED){
                            var contentModel = item.document.toObject(ContentModel::class.java)
                            contentModels.add(contentModel!!)
                            contentUidsList.add(item.document.id)
                        }

                    }
                    notifyDataSetChanged()
                }
            //데이터를 불러오는 코드를 넣어주도록 할께요

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
            //행 하나에 어떤 디자인의 XML 넣을지 설정해는 코드 보면 될것 같습니다.
            var view = ItemDetailBinding.inflate(LayoutInflater.from(parent.context),parent,false)

            return DetailViewHolder(view)
        }

        override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
            //데이터를 바인딩
            var contentModel = contentModels[position]
            var viewHolder = holder.binding
            viewHolder.profileTextview.text = contentModel.userId
            viewHolder.likeTextview.text = "Likes " + contentModel.favoriteCount
            viewHolder.explainTextview.text = contentModel.explain
            //상대방 유저페이지 이동

            Glide.with(holder.itemView.context).load(contentModel.imageUrl).into(viewHolder.contentImageview)


        }


        override fun getItemCount(): Int {

            return contentModels.size
        }

    }
}