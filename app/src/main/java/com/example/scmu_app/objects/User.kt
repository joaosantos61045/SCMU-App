package com.example.scmu_app.objects

data class User(
    val id: String,
    val boards: MutableList<String>
) {
    override fun toString(): String {
        return "User[id=$id, boards=$boards]"
    }
}
