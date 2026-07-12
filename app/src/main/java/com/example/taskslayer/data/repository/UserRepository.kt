package com.example.taskslayer.data.repository

import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.domain.model.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Repositório responsável por gerenciar os dados do usuário (Guerreiro) no Firestore.
 * Inclui operações de perfil, estatísticas e o sistema de "reputação" (XP/HP).
 */
class UserRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = db.collection("usuarios")

    /**
     * Busca os dados completos de um usuário a partir do seu UID.
     */
    fun buscarUsuario(uid: String, onSucesso: (User) -> Unit, onErro: (Exception) -> Unit) {
        usersCollection.document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
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

    /**
     * Salva o perfil do usuário no banco. Utilizado no momento do cadastro.
     */
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

    /**
     * Modifica um contador específico nas estatísticas do usuário (ex: tasksCriadas).
     * @param modificacao Pode ser "increment" ou "decrement".
     */
    fun modificarEstatisticaUsuario(
        uid: String,
        campo: String,
        quantidade: Long,
        modificacao: String
    ) {
        if (modificacao == "increment") {
            usersCollection.document(uid).update(campo, FieldValue.increment(quantidade))
        } else if (modificacao == "decrement") {
            usersCollection.document(uid).update(campo, FieldValue.increment(-quantidade))
        }
    }

    /**
     * Calcula e atualiza a reputação (statusAtual) e o contador de tarefas concluídas.
     * Utiliza uma transação do Firestore para garantir que a atualização seja atômica.
     */
    fun computarProgressoTarefa(uid: String, isConcluido: Boolean, dificuldade: Dificulty) {
        val docRef = usersCollection.document(uid)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            if (!snapshot.exists()) return@runTransaction

            val statusAtual = snapshot.getLong("statusAtual") ?: 0L
            val tasksConcluidas = snapshot.getLong("tasksConcluidas") ?: 0L

            // Define ganho/perda de pontos com base na dificuldade
            val pontosBase = when (dificuldade) {
                Dificulty.TRIVIAL -> 2L
                Dificulty.FACIL -> 5L
                Dificulty.MEDIO -> 10L
                Dificulty.DIFICIL -> 20L
                else -> 0L
            }

            // Define se os pontos serão somados ou subtraídos
            val modificador = if (isConcluido) 1L else -1L

            // Garante que os valores fiquem dentro de limites aceitáveis (0 a 100 para status)
            val novasConcluidas = (tasksConcluidas + modificador).coerceAtLeast(0L)
            val novosPontos = (statusAtual + (pontosBase * modificador)).coerceIn(0L, 100L)

            transaction.update(docRef, "tasksConcluidas", novasConcluidas)
            transaction.update(docRef, "statusAtual", novosPontos)
        }.addOnFailureListener { e ->
            android.util.Log.e("UserRepository", "Erro na transação de pontos da tarefa", e)
        }
    }

    /**
     * Atualiza a reputação do usuário baseada na prática de um hábito (Positivo ou Negativo).
     */
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

            // Hábitos positivos aumentam reputação, negativos diminuem
            val modificador = if (isPositivo) 1L else -1L
            val novosPontos = (statusAtual + (pontosBase * modificador)).coerceIn(0L, 100L)

            transaction.update(docRef, "statusAtual", novosPontos)
        }.addOnFailureListener { e ->
            android.util.Log.e("UserRepository", "Erro na transação de pontos do hábito", e)
        }
    }
}
