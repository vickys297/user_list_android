package com.example.randomuserlisting.utils

import com.example.randomuserlisting.model.UserModel

interface AppInterface {

    interface UserList {
        fun onUserClick(item: UserModel)
    }

}