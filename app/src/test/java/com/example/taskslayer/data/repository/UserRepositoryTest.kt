package com.example.taskslayer.data.repository

import com.example.taskslayer.domain.model.User
import com.example.taskslayer.domain.model.Dificulty
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import io.mockk.*
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {

    private lateinit var db: FirebaseFirestore
    private lateinit var repository: UserRepository
    private lateinit var usersCollectionRef: CollectionReference
    private lateinit var userDocRef: DocumentReference

    @Before
    fun setup() {
        db = mockk(relaxed = true)
        usersCollectionRef = mockk(relaxed = true)
        userDocRef = mockk(relaxed = true)

        every { db.collection("usuarios") } returns usersCollectionRef
        every { usersCollectionRef.document(any()) } returns userDocRef

        repository = UserRepository(db)
    }

    @Test
    fun `salvarUsuario testando o registro de usuario`() {
        // Given
        val user = User(nome = "Test", email = "test@test.com", userID = "uid_123")
        every { userDocRef.set(any()) } returns mockk(relaxed = true)

        // When
        repository.salvarUsuario(user, {}, {})

        // Then
        verify { usersCollectionRef.document("uid_123") }
        verify { userDocRef.set(any()) }
    }

    @Test
    fun `modificarEstatisticaUsuario testando registro de tarefa nas estatisticas`() {
        // Given
        mockkStatic(FieldValue::class)
        val mockIncrement = mockk<FieldValue>(relaxed = true)
        every { FieldValue.increment(any<Long>()) } returns mockIncrement
        every { userDocRef.update(any<String>(), any()) } returns mockk(relaxed = true)

        // When
        repository.modificarEstatisticaUsuario("uid_123", "habitosAtivos", 5L, "increment")

        // Then
        verify { usersCollectionRef.document("uid_123") }
        verify { userDocRef.update("habitosAtivos", any()) }

        unmockkStatic(FieldValue::class)
    }

    @Test
    fun `computarProgressoHabito testando se o habito esta sendo contado`() {
        // Given
        every { db.runTransaction<Any>(any()) } returns mockk(relaxed = true)

        // When
        repository.computarProgressoHabito("uid_123", true, Dificulty.MEDIO)

        // Then
        verify { db.runTransaction<Any>(any()) }
    }
}