package com.example.tawsila

data class ClientDTO(
    val id: Long ,
    val name: String = "",
    val email: String = "",
    val password: String? = "",
    val profileImage: Any?,
    val roleName: String = ""



)
