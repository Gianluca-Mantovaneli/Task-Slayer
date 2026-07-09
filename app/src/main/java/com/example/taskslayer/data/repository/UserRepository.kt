package com.example.taskslayer.data.repository

import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.domain.model.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("usuarios")

    fun buscarUsuario(uid: String, onSucesso: (User) -> Unit, onErro: (Exception) -> Unit) {
        usersCollection.document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // O Firestore converte o JSON do banco de volta para objeto User
                    val usuario = document.toObject(User::class.java)
                    if (usuario != null) {
                        onSucesso(usuario)
                    }
                } else {
                    onErro(Exception("Usuário não encontrado no banco."))
                }
            }
            .addOnFailureListener { exception ->
                onErro(exception)
            }
    }

    fun salvarUsuario(user: User, onSucesso: () -> Unit, onErro: (Exception) -> Unit) {
        val uid = user.userID
        if (uid == null) {
            onErro(Exception("Não é possível salvar um usuário sem ID."))
            return
        }

        usersCollection.document(uid)
            .set(user)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { exception ->
                onErro(exception)
            }
    }

    fun modificarEstatisticaUsuario(
        uid: String,
        campo: String,
        quantidade: Long,
        modificacao: String
    ) {
        usersCollection.document(uid)
        if (modificacao == "increment") {
            usersCollection.document(uid).update(campo, FieldValue.increment(quantidade))
        } else if (modificacao == "decrement") {
            usersCollection.document(uid).update(campo, FieldValue.increment(-quantidade))
        }

    }

    fun computarProgressoTarefa(uid: String, isConcluido: Boolean, dificuldade: Dificulty) {
        val docRef = usersCollection.document(uid)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            if (!snapshot.exists()) return@runTransaction

            val statusAtual = snapshot.getLong("statusAtual") ?: 0L
            val tasksConcluidas = snapshot.getLong("tasksConcluidas") ?: 0L

            // Define os pontos com base na dificuldade escolhida
            val pontosBase = when (dificuldade) {
                Dificulty.TRIVIAL -> 2L
                Dificulty.FACIL -> 5L
                Dificulty.MEDIO -> 10L
                Dificulty.DIFICIL -> 20L
                else -> 0L
            }

            // Se marcou (true) soma 1L, se desmarcou (false) subtrai -1L
            val modificador = if (isConcluido) 1L else -1L

            // Calcula os novos valores aplicando travas de segurança (clamping)
            val novasConcluidas = (tasksConcluidas + modificador).coerceAtLeast(0L)
            val novosPontos = (statusAtual + (pontosBase * modificador)).coerceIn(0L, 100L)

            // Atualiza os dois campos de forma atômica no mesmo milissegundo
            transaction.update(docRef, "tasksConcluidas", novasConcluidas)
            transaction.update(docRef, "statusAtual", novosPontos)
        }.addOnFailureListener { e ->
            android.util.Log.e("UserRepository", "Erro na transação de pontos da tarefa", e)
        }
    }

    fun computarProgressoHabito(uid: String, isPositivo: Boolean, dificuldade: Dificulty) {
        val docRef = usersCollection.document(uid)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            if (!snapshot.exists()) return@runTransaction

            val statusAtual = snapshot.getLong("statusAtual") ?: 0L

            val pontosBase = when (dificuldade) {
                Dificulty.TRIVIAL -> 2L
                Dificulty.FACIL -> 5L
                Dificulty.MEDIO -> 10L
                Dificulty.DIFICIL -> 20L
                else -> 2L
            }

            // Clique no botão de mais (+) soma, no botão de menos (-) subtrai
            val modificador = if (isPositivo) 1L else -1L
            val novosPontos = (statusAtual + (pontosBase * modificador)).coerceIn(0L, 100L)

            transaction.update(docRef, "statusAtual", novosPontos)
        }.addOnFailureListener { e ->
            android.util.Log.e("UserRepository", "Erro na transação de pontos do hábito", e)
        }
    }
}