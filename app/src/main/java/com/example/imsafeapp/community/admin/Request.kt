package com.example.imsafeapp.community.admin

data class Request(val userId: String? = null, val constituency: String? = null, val userName: String? = null, val phoneNumber: String? = null, val approved: Boolean = false)
