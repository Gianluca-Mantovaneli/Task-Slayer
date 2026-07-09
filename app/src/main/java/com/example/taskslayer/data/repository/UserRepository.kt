package com.example.taskslayer.data.repository

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
}