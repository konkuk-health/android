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
import com.example.lsproject2.databinding.FragmentDetailViewBinding
import com.example.lsproject2.databinding.ItemDetailBinding
import com.example.lsproject2.model.ContentModel
import com.example.lsproject2.model.FollowModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class DetailViewFragment : Fragment() {
    lateinit var binding : FragmentDetailViewBinding
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_view,container,false)
        uid = FirebaseAuth.getInstance().currentUser?.uid!!
        firestore.collection("users").document(uid).get().addOnSuccessListener { result ->
            var followModel = result.toObject(FollowModel::class.java)
           // if (followModel?.followings?.keys != null && followModel.followings.keys.size > 0) {

                binding.detailviewRecyclerveiw.adapter = DetailviewRecyclerviewAdapter()
                binding.detailviewRecyclerveiw.layoutManager = LinearLayoutManager(activity)
           // }
        }

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
            viewHolder.favoriteImageview.setOnClickListener {
                eventFavorite(position)
            }

            viewHolder.profileTextview.setOnClickListener {
                var userFragment = UserFragment()
                var bundle = Bundle()
                bundle.putString("dUid",contentModel.uid)
                bundle.putString("userId",contentModel.userId)
                userFragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,userFragment)?.commit()

            }

            Glide.with(holder.itemView.context).load(contentModel.imageUrl).into(viewHolder.contentImageview)

            if(contentModel.favorites.containsKey(uid)){
                //이미 좋아요를 누른상태
                viewHolder.favoriteImageview.setImageResource(R.drawable.ic_favorite)
            }else{
                viewHolder.favoriteImageview.setImageResource(R.drawable.ic_favorite_border)
            }
        }


        override fun getItemCount(): Int {

            return contentModels.size
        }

        fun eventFavorite(position : Int){
            var docId = contentUidsList[position]
            var tsDoc = firestore.collection("images").document(docId)
            firestore.runTransaction {
                    transition ->
                var contentDTO = transition.get(tsDoc).toObject(ContentModel::class.java)
                if(contentDTO!!.favorites.containsKey(uid)){
                    //좋아요 누른 상태
                    contentDTO.favoriteCount = contentDTO.favoriteCount - 1
                    contentDTO.favorites.remove(uid)

                }else{
                    //좋아요를 누르지 않은 상태
                    contentDTO.favoriteCount = contentDTO.favoriteCount + 1
                    contentDTO.favorites[uid] = true
                    //favorteAlarm(contentDTO.uid!!)

                }
                transition.set(tsDoc,contentDTO)
            }

        }

    }
}