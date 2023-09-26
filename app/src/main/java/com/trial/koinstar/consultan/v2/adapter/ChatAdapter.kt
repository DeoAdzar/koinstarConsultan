package com.trial.koinstar.consultan.v2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trial.koinstar.consultan.v2.databinding.ReceiveChatLayoutBinding
import com.trial.koinstar.consultan.v2.databinding.SenderChatLayoutBinding
import com.trial.koinstar.consultan.v2.model.chatObject


class ChatAdapter(chatMessageList: List<chatObject>, senderId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private val chatMessageList: List<chatObject>
    private val senderId: String

    init {
        this.chatMessageList = chatMessageList
        this.senderId = senderId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            SentMessageViewHolder(
                SenderChatLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        } else {
            ReceiveMessageViewHolder(
                ReceiveChatLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            (holder as SentMessageViewHolder).setData(chatMessageList[position])
        } else {
            (holder as ReceiveMessageViewHolder).setData(chatMessageList[position])
        }
    }

    override fun getItemCount(): Int {
        return chatMessageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatMessageList[position].senderId.equals(senderId)) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVE
        }
    }

    internal class SentMessageViewHolder(senderChatLayoutBinding: SenderChatLayoutBinding) :
        RecyclerView.ViewHolder(senderChatLayoutBinding.root) {
        private val binding: SenderChatLayoutBinding

        init {
            binding = senderChatLayoutBinding
        }

        fun setData(chatMessage: chatObject) {
            binding.textPesan2.text = chatMessage.message
            binding.timeReceive2.text = chatMessage.dateTime
        }
    }

    internal class ReceiveMessageViewHolder(receiveChatLayoutBinding: ReceiveChatLayoutBinding) :
        RecyclerView.ViewHolder(receiveChatLayoutBinding.root) {
        private val binding: ReceiveChatLayoutBinding

        init {
            binding = receiveChatLayoutBinding
        }

        fun setData(chatMessage: chatObject) {
            binding.textPesan.text = chatMessage.message
            binding.timeReceive.text = chatMessage.dateTime
        }
    }

    companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVE = 2
    }
}