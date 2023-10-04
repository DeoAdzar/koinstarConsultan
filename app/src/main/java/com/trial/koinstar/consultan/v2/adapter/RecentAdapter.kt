package com.trial.koinstar.consultan.v2.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trial.koinstar.consultan.v2.R
import com.trial.koinstar.consultan.v2.activity.ChatActivity
import com.trial.koinstar.consultan.v2.databinding.RvItemChatBinding
import com.trial.koinstar.consultan.v2.model.chatObject


class RecentAdapter(chatMessageList: List<chatObject>, ctx: Context) :
    RecyclerView.Adapter<RecentAdapter.ConversionViewHolder?>() {
    private val chatMessageList: List<chatObject>

    //    private final ConversionListener conversionListener;
    var ctx: Context

    init {
        this.chatMessageList = chatMessageList
        this.ctx = ctx
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        return ConversionViewHolder(
            RvItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ConversionViewHolder, position: Int) {
        holder.setData(chatMessageList[position],ctx)
        holder.binding.root.setOnClickListener { _ ->
            val i = Intent(ctx, ChatActivity::class.java)
            i.putExtra("idKonsultan", chatMessageList[position].conversionId)
            i.putExtra("namaKonsultan", chatMessageList[position].conversionName)
            i.putExtra("imageKonsultan", chatMessageList[position].conversionImage)
            ctx.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return chatMessageList.size
    }

    class ConversionViewHolder(itemChatBinding: RvItemChatBinding) :
        RecyclerView.ViewHolder(itemChatBinding.root) {
        var binding: RvItemChatBinding

        init {
            binding = itemChatBinding
        }

        fun setData(chatMessage: chatObject,ctx: Context) {
            Log.d("image", "setData: ${chatMessage.conversionImage}")
            if (chatMessage.conversionImage.isNullOrEmpty()||chatMessage.conversionImage == "null"||chatMessage.conversionImage == ""||chatMessage.conversionImage.isNullOrBlank()){
                binding.ivRecentChat.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.person))
            }else {
                Glide.with(ctx)
                    .load(chatMessage.conversionImage)
                    .placeholder(R.drawable.person) // Placeholder image while loading
                    .into(binding.ivRecentChat)
            }
            binding.tvNameRecent.text = chatMessage.conversionName
            binding.tvMessageRecent.text = chatMessage.message
            //            binding.getRoot().setOnClickListener(view -> {
//                User user = new User();
//                user.id = chatMessage.conversionId;
//                user.name = chatMessage.conversionName;
//                user.image = chatMessage.conversionImage;
//                conversionListener.onConversionClick(user);
//            });
        }
    }


}