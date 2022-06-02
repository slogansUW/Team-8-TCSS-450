package edu.uw.tcss450.group8.chatapp.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.group8.chatapp.R;
import edu.uw.tcss450.group8.chatapp.databinding.FragmentChatroomCardBinding;
import edu.uw.tcss450.group8.chatapp.model.NewMessageCountViewModel;
import edu.uw.tcss450.group8.chatapp.ui.comms.chat.Message;
import edu.uw.tcss450.group8.chatapp.ui.comms.chat.MessageListViewModel;
import edu.uw.tcss450.group8.chatapp.ui.comms.chatrooms.Chatroom;

/**
 * RecyclerViewAdapter for message.
 * Adapted from original code by Charles Bryan.
 *
 * @author Charles Bryan
 * @author Levi McCoy
 * @author Shilnara Dam
 * @author Sean Logan
 * @version 5/29/22
 */
public class HomeChatroomViewRecyclerAdapter extends RecyclerView.Adapter<HomeChatroomViewRecyclerAdapter.ChatroomViewHolder> {

    //Store all of the blogs to present
    private final List<Chatroom> mChatroom;

    private final HomeFragment mParent;

    private MessageListViewModel mMessageModel;

    private NewMessageCountViewModel mNewMessageModel;

    /**
     * Constructor for HomeChatroomViewRecyclerAdapter
     *
     * @param items  list of chatroom
     * @param parent HomeFragment
     */
    public HomeChatroomViewRecyclerAdapter(List<Chatroom> items, HomeFragment parent) {
        this.mChatroom = items;
        this.mParent = parent;
        this.mMessageModel = new ViewModelProvider(mParent.getActivity()).get(MessageListViewModel.class);
        this.mNewMessageModel = new ViewModelProvider(mParent.getActivity()).get(NewMessageCountViewModel.class);
    }

    @NonNull
    @Override
    public ChatroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatroomViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_chatroom_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatroomViewHolder holder, int position) {
        holder.setChatroom(mChatroom.get(position));
    }

    @Override
    public int getItemCount() {
        return this.mChatroom.size();
    }

    /**
     * Objects from this class represent an Individual row View from the List * of rows in the
     * Chatroom Recycler View.
     */
    public class ChatroomViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentChatroomCardBinding binding;
        private Chatroom mChatroom;
        private Button openChat;
        private TextView chatId;
        private TextView chatName;

        /**
         * Constructor for View Holder
         *
         * @param view View
         */
        public ChatroomViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatroomCardBinding.bind(view);

            chatId = mView.findViewById(R.id.text_chatid);
            chatId.setVisibility(View.INVISIBLE);
            chatName = mView.findViewById(R.id.text_title);
            binding.layoutInner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mParent.homeStartChat(Integer.parseInt(chatId.getText().toString()), chatName.getText().toString());
                    NewMessageCountViewModel mNewMessageModel = new ViewModelProvider(mParent.getActivity()).get(NewMessageCountViewModel.class);
                    mNewMessageModel.clear(Integer.parseInt(chatId.getText().toString()));
                }
            });
        }

        /**
         * Sets the chat room id and name
         *
         * @param chatroom Chatroom
         */
        void setChatroom(final Chatroom chatroom) {
            mChatroom = chatroom;
            binding.textTitle.setText(chatroom.getChatRoomName());
            binding.textChatid.setText(chatroom.getChatRoomId());
            int chatId = Integer.parseInt(chatroom.getChatRoomId());
            mMessageModel.addMessageObserver(chatId, mParent.getViewLifecycleOwner(), messages -> {
                List<Message> messageList = mMessageModel.getMessageListByChatId(chatId);
                if (!messageList.isEmpty()) {
                    String newMessage = messageList.get(messageList.size() - 1).getMessage();
                    binding.textPreview.setText(newMessage);
                }
            });

            mNewMessageModel.addMessageCountObserver(chatId, mParent.getViewLifecycleOwner(), count -> {

                if (count == 0) {

                    binding.textUnread.setVisibility(View.INVISIBLE);
                } else {
                    binding.textUnread.setVisibility(View.VISIBLE);
                    String str = String.valueOf(count);
                    binding.textUnread.setText(str);

                }

            });

        }
    }
}
