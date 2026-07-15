package com.example.taskslayer.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.taskslayer.domain.model.Todo
import com.example.taskslayer.domain.model.Dailie
import com.example.taskslayer.domain.model.Habit

/**
 * Repositório responsável por todas as operações de persistência relacionadas a tarefas,
 * missões diárias e hábitos no Firebase Firestore.
 */
class TaskRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Retorna a referência do documento do usuário no Firestore.
     */
    private fun getUsuarioDocument(uid: String) = db.collection("usuarios").document(uid)

    // --- Operações para TO-DOs (Tarefas Únicas) ---

    /**
     * Salva ou atualiza uma tarefa To-Do no banco de dados.
     */
    fun salvarTodo(uid: String, todo: Todo, onSucesso: () -> Unit, onErro: (Exception) -> Unit) {
        val colecaoTodos = getUsuarioDocument(uid).collection("todos")
        // Se o ID estiver em branco, o Firestore gera um novo ID automaticamente.
        val documento =
            if (todo.id.isBlank()) colecaoTodos.document() else colecaoTodos.document(todo.id)
        val todoComId = todo.copy(id = documento.id)

        documento.set(todoComId)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { onErro(it) }
    }

    /**
     * Lista todas as tarefas To-Do do usuário.
     */
    fun listarTodos(uid: String, onSucesso: (List<Todo>) -> Unit, onErro: (Exception) -> Unit) {
        getUsuarioDocument(uid).collection("todos")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val lista = querySnapshot.toObjects(Todo::class.java)
                onSucesso(lista)
            }
            .addOnFailureListener { onErro(it) }
    }

    /**
     * Busca uma tarefa To-Do específica pelo seu ID.
     */
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

    /**
     * Deleta uma tarefa To-Do permanentemente.
     */
    fun deletarTodo(
        uid: String, taskId: String, onSucesso: () -> Unit, onErro: (Exception) -> Unit
    ) {
        getUsuarioDocument(uid).collection("todos").document(taskId)
            .delete()
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { exception -> onErro(exception) }
    }

    // --- Operações para DAILIES (Missões Diárias) ---

    /**
     * Salva ou atualiza uma missão diária.
     */
    fun salvarDailie(
        uid: String, dailie: Dailie, onSucesso: () -> Unit, onErro: (Exception) -> Unit
    ) {
        val colecaoDailies = getUsuarioDocument(uid).collection("dailies")
        val documento =
            if (dailie.id.isBlank()) colecaoDailies.document() else colecaoDailies.document(dailie.id)
        val dailieComId = dailie.copy(id = documento.id)

        documento.set(dailieComId)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { onErro(it) }
    }

    /**
     * Lista todas as missões diárias do usuário.
     */
    fun listarDailies(uid: String, onSucesso: (List<Dailie>) -> Unit, onErro: (Exception) -> Unit) {
        getUsuarioDocument(uid).collection("dailies")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val lista = querySnapshot.toObjects(Dailie::class.java)
                onSucesso(lista)
            }
            .addOnFailureListener { onErro(it) }
    }

    /**
     * Busca uma missão diária específica pelo seu ID.
     */
    fun buscarDailiePorId(
        uid: String, taskId: String, onSucesso: (Dailie) -> Unit, onErro: (Exception) -> Unit
    ) {
        getUsuarioDocument(uid).collection("dailies").document(taskId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val dailie = documentSnapshot.toObject(Dailie::class.java)

                if (dailie != null && documentSnapshot.exists()) {
                    val dailieComId = dailie.copy(id = documentSnapshot.id)
                    onSucesso(dailieComId)
                } else {
                    onErro(Exception("Missão diária não encontrada!"))
                }
            }
            .addOnFailureListener { exception -> onErro(exception) }
    }

    /**
     * Deleta uma missão diária permanentemente.
     */
    fun deletarDailie(
        uid: String, taskId: String, onSucesso: () -> Unit, onErro: (Exception) -> Unit
    ) {
        getUsuarioDocument(uid).collection("dailies").document(taskId)
            .delete()
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { exception -> onErro(exception) }
    }

    // --- Operações para HABITS (Hábitos) ---

    /**
     * Salva ou atualiza um hábito.
     */
    fun salvarHabit(uid: String, habit: Habit, onSucesso: () -> Unit, onErro: (Exception) -> Unit) {
        val colecaoHabits = getUsuarioDocument(uid).collection("habits")
        val documento =
            if (habit.id.isBlank()) colecaoHabits.document() else colecaoHabits.document(habit.id)
        val habitComId = habit.copy(id = documento.id)

        documento.set(habitComId)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { onErro(it) }
    }

    /**
     * Lista todos os hábitos do usuário.
     */
    fun listarHabits(uid: String, onSucesso: (List<Habit>) -> Unit, onErro: (Exception) -> Unit) {
        getUsuarioDocument(uid).collection("habits")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val lista = querySnapshot.toObjects(Habit::class.java)
                onSucesso(lista)
            }
            .addOnFailureListener { onErro(it) }
    }

    /**
     * Busca um hábito específico pelo seu ID.
     */
    fun buscarHabitPorId(
        uid: String, taskId: String, onSucesso: (Habit) -> Unit, onErro: (Exception) -> Unit
    ) {
        getUsuarioDocument(uid).collection("habits").document(taskId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val habit = documentSnapshot.toObject(Habit::class.java)

                if (habit != null && documentSnapshot.exists()) {
                    val habitComId = habit.copy(id = documentSnapshot.id)
                    onSucesso(habitComId)
                } else {
                    onErro(Exception("Hábito não encontrado!"))
                }
            }
            .addOnFailureListener { exception -> onErro(exception) }
    }

    /**
     * Deleta um hábito permanentemente.
     */
    fun deletarHabit(
        uid: String, taskId: String, onSucesso: () -> Unit, onErro: (Exception) -> Unit
    ) {
        getUsuarioDocument(uid).collection("habits").document(taskId)
            .delete()
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { exception -> onErro(exception) }
    }

    // --- Operações Genéricas ---

    /**
     * Atualiza o campo 'done' (concluído) de qualquer tipo de tarefa.
     */
    fun atualizarStatusTarefa(
        uid: String,
        taskId: String,
        tipoColecao: String,
        novoStatus: Boolean,
        onSucesso: () -> Unit,
        onErro: (Exception) -> Unit
    ) {
        getUsuarioDocument(uid).collection(tipoColecao).document(taskId)
            .update("done", novoStatus)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { onErro(it) }
    }

    /**
     * Reseta o status de uma missão diária e atualiza a data do último reset.
     */
    fun resetDailieStatus(
        uid: String,
        taskId: String,
        novoStatus: Boolean,
        lastResetDate: String,
        onSucesso: () -> Unit = {},
        onErro: (Exception) -> Unit = {}
    ) {
        val updates = mapOf(
            "done" to novoStatus,
            "lastReset" to lastResetDate
        )
        getUsuarioDocument(uid).collection("dailies").document(taskId)
            .update(updates)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { onErro(it) }
    }
}
