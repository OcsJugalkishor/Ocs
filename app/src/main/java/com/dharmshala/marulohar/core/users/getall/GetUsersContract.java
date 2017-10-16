package com.dharmshala.marulohar.core.users.getall;


import com.dharmshala.marulohar.model.newUser;

import java.util.List;


public interface GetUsersContract {
    interface View {
        void onGetAllUsersSuccess(List<newUser> users);

        void onGetAllUsersFailure(String message);

        void onGetChatUsersSuccess(List<newUser> users);

        void onGetChatUsersFailure(String message);
    }

    interface Presenter {
        void getAllUsers();

        void getChatUsers();
    }

    interface Interactor {
        void getAllUsersFromFirebase();

        void getChatUsersFromFirebase();
    }

    interface OnGetAllUsersListener {
        void onGetAllUsersSuccess(List<newUser> users);

        void onGetAllUsersFailure(String message);
    }

    interface OnGetChatUsersListener {
        void onGetChatUsersSuccess(List<newUser> users);

        void onGetChatUsersFailure(String message);
    }
}
