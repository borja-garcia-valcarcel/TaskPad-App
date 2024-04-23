package com.example.taskpad.utils

// datos que contiene cada tarea

data class TaskData(val taskId: String, var task: String, var dueDate: String? = null)
