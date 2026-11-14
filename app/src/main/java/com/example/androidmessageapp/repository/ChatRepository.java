package com.example.androidmessageapp.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.androidmessageapp.data.local.AppDatabase;
import com.example.androidmessageapp.data.local.dao.ConversationDao;
import com.example.androidmessageapp.data.local.dao.MessageDao;
import com.example.androidmessageapp.data.local.entity.ConversationEntity;
import com.example.androidmessageapp.data.local.entity.MessageEntity;
import com.example.androidmessageapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatRepository {

    private final ConversationDao conversationDao;
    private final MessageDao messageDao;
    private final LiveData<List<ConversationEntity>> allConversations;

    public ChatRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        conversationDao = db.conversationDao();
        messageDao = db.messageDao();
        allConversations = conversationDao.getAllConversations();
    }

    // --- CÁC HÀM ĐỌC DỮ LIỆU (Trả về LiveData cho UI quan sát) ---

    public LiveData<List<ConversationEntity>> getAllConversations() {
        return allConversations;
    }

    public LiveData<ConversationEntity> getConversationById(String conversationId) {
        return conversationDao.getConversationById(conversationId);
    }

    public LiveData<List<MessageEntity>> getMessagesByConversation(String conversationId) {
        return messageDao.getMessagesByConversation(conversationId);
    }

    // --- CÁC HÀM GHI DỮ LIỆU (Chạy trên luồng nền databaseWriteExecutor) ---

    // Gửi tin nhắn mới (Lưu cục bộ trước - Offline First)
    public void sendMessage(String conversationId, String content, String type) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long now = System.currentTimeMillis();
            String msgId = UUID.randomUUID().toString();

            // 1. Tạo tin nhắn mới với trạng thái SENDING (sau này có server sẽ đổi thành SENT)
            MessageEntity newMessage = new MessageEntity(
                    msgId,
                    conversationId,
                    Constants.CURRENT_USER_ID,
                    type,
                    content,
                    Constants.STATUS_SENT, // Tạm để SENT trong Giai đoạn 1 chạy offline
                    now
            );
            messageDao.insert(newMessage);

            // 2. Cập nhật dòng preview tin nhắn cuối ở màn hình danh sách bên ngoài
            String previewText = type.equals(Constants.MSG_TYPE_IMAGE) ? "[Hình ảnh]" : content;
            conversationDao.updateLastMessage(conversationId, previewText, now);
        });
    }

    public void markConversationAsRead(String conversationId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            conversationDao.markAsRead(conversationId);
        });
    }

    // --- BƠM DỮ LIỆU MẪU (Chạy 1 lần để có dữ liệu test giao diện) ---
    public void seedSampleData() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long now = System.currentTimeMillis();

            List<ConversationEntity> sampleConvs = new ArrayList<>();
            sampleConvs.add(new ConversationEntity(
                    "conv_1", "Minh Anh", "",
                    "Tối nay cậu làm xong phần UI chưa?", now - 60000, 2, true
            ));
            sampleConvs.add(new ConversationEntity(
                    "conv_2", "Nhóm Đồ Án Android", "",
                    "Tuần sau bảo vệ rồi anh em ơi!", now - 3600000, 0, true
            ));
            sampleConvs.add(new ConversationEntity(
                    "conv_3", "Thầy Bằng (Hướng dẫn)", "",
                    "Nhớ nộp file zip kèm thư mục .git nhé.", now - 86400000, 1, false
            ));
            conversationDao.insertAll(sampleConvs);

            List<MessageEntity> sampleMsgs = new ArrayList<>();
            sampleMsgs.add(new MessageEntity(
                    "m1", "conv_1", "user_minh_anh",
                    Constants.MSG_TYPE_TEXT, "Chào cậu, dự án đến đâu rồi?", Constants.STATUS_READ, now - 120000
            ));
            sampleMsgs.add(new MessageEntity(
                    "m2", "conv_1", Constants.CURRENT_USER_ID,
                    Constants.MSG_TYPE_TEXT, "Tớ vừa setup xong Room Database rồi!", Constants.STATUS_READ, now - 90000
            ));
            sampleMsgs.add(new MessageEntity(
                    "m3", "conv_1", "user_minh_anh",
                    Constants.MSG_TYPE_TEXT, "Tối nay cậu làm xong phần UI chưa?", Constants.STATUS_READ, now - 60000
            ));
            messageDao.insertAll(sampleMsgs);
        });
    }
}