package com.example.taskslayer.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.taskslayer.domain.model.Todo
import com.example.taskslayer.domain.model.Dailie
import com.example.taskslayer.domain.model.Habit

class TaskRepository {

    private val db = FirebaseFirestore.getInstance()
    private fun getUsuarioDocument(uid: String) = db.collection("usuarios").document(uid)

    fun salvarTodo(uid: String, todo: Todo, onSucesso: () -> Unit, onErro: (Exception) -> Unit) {
        val colecaoTodos = getUsuarioDocument(uid).collection("todos")
        val documento =
            if (todo.id.isBlank()) colecaoTodos.document() else colecaoTodos.document(todo.id)
        val todoComId = todo.copy(id = documento.id)

        documento.set(todoComId)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { onErro(it) }
    }

    fun listarTodos(uid: String, onSucesso: (List<Todo>) -> Unit, onErro: (Exception) -> Unit) {
        getUsuarioDocument(uid).collection("todos")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val lista = querySnapshot.toObjects(Todo::class.java)
                onSucesso(lista)
            }
            .addOnFailureListener { onErro(it) }
    }

    fun buscarTodoPorId(
        uid: String, taskId: String, onSucesso: (Todo) -> Unit, onErro: (Exception) -> Unit
    ) {
        getUsuarioDocument(uid).collection("todos").document(taskId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val todo = documentSnapshot.toObject(Todo::class.java)

                if (todo != null && documentSnapshot.exists()) {
                    val todoComId = todo.copy(id = documentSnapshot.id)
                    onSucesso(todoComId)
                } else {
                    onErro(Exception("Task não encontrada!"))
                }
            }
            .addOnFailureListener { exception ->
                onErro(exception)
            }
    }

    fun deletarTodo(
        uid: String,
        taskId: String,
        onSucesso: () -> Unit,
        onErro: (Exception) -> Unit
    ) {

        db.collection("usuarios")
            .document(uid)
            .collection("todos")
            .document(taskId)
            .delete()
            .addOnSuccessListener {
                onSucesso()
            }
            .addOnFailureListener { exception ->
                onErro(exception)
            }
    }

    fun salvarDailie(
        uid: String,
        dailie: Dailie,
        onSucesso: () -> Unit,
        onErro: (Exception) -> Unit
    ) {
        val colecaoDailies = getUsuarioDocument(uid).collection("dailies")
        val documento =
            if (dailie.id.isBlank()) colecaoDailies.document() else colecaoDailies.document(dailie.id)
        val dailieComId = dailie.copy(id = documento.id)

        documento.set(dailieComId)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { onErro(it) }
    }

    fun listarDailies(uid: String, onSucesso: (List<Dailie>) -> Unit, onErro: (Exception) -> Unit) {
        getUsuarioDocument(uid).collection("dailies")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val lista = querySnapshot.toObjects(Dailie::class.java)
                onSucesso(lista)
            }
            .addOnFailureListener { onErro(it) }
    }

    fun salvarHabit(uid: String, habit: Habit, onSucesso: () -> Unit, onErro: (Exception) -> Unit) {
        val colecaoHabits = getUsuarioDocument(uid).collection("habits")
        val documento =
            if (habit.id.isBlank()) colecaoHabits.document() else colecaoHabits.document(habit.id)
        val habitComId = habit.copy(id = documento.id)

        documento.set(habitComId)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { onErro(it) }
    }

    fun listarHabits(uid: String, onSucesso: (List<Habit>) -> Unit, onErro: (Exception) -> Unit) {
        getUsuarioDocument(uid).collection("habits")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val lista = querySnapshot.toObjects(Habit::class.java)
                onSucesso(lista)
            }
            .addOnFailureListener { onErro(it) }
    }

    fun atualizarStatusTarefa(
        uid: String,
        taskId: String,
        tipoColecao: String,
        novoStatus: Boolean,
        onSucesso: () -> Unit,
        onErro: (Exception) -> Unit
    ) {
        // tipoColecao deve ser "todos", "dailies" ou "habits"
        getUsuarioDocument(uid).collection(tipoColecao).document(taskId)
            .update(
                "done",
                novoStatus
            ) // 🪄 Atualiza cirurgicamente apenas o campo 'done' na nuvem!
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { onErro(it) }
    }
}