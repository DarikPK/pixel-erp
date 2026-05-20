package pe.pixelstudio.pixelerp.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pe.pixelstudio.pixelerp.data.model.Negocio
import pe.pixelstudio.pixelerp.data.model.Usuario
import java.security.MessageDigest

class FirebaseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val negociosCollection = firestore.collection("Negocios")

    suspend fun getNegocioPorNombre(nombre: String): Negocio? {
        val query = negociosCollection.whereEqualTo("nombreComercial", nombre).get().await()
        return query.documents.firstOrNull()?.toObject(Negocio::class.java)
    }

    suspend fun getNegocioById(id: String): Negocio? {
        val doc = negociosCollection.document(id).get().await()
        return doc.toObject(Negocio::class.java)
    }

    suspend fun getUsuarioEnNegocio(negocioId: String, username: String): Usuario? {
        val query = negociosCollection.document(negocioId)
            .collection("usuarios")
            .whereEqualTo("usuario", username)
            .get().await()
        return query.documents.firstOrNull()?.toObject(Usuario::class.java)
    }

    suspend fun obtenerUsuariosPorNegocio(negocioId: String): List<Usuario> {
        val query = negociosCollection.document(negocioId)
            .collection("usuarios")
            .get().await()
        return query.toObjects(Usuario::class.java)
    }

    suspend fun crearUsuarioEnNegocio(negocioId: String, usuario: Usuario) {
        val docRef = negociosCollection.document(negocioId).collection("usuarios").document()
        val nuevoUsuario = usuario.copy(id = docRef.id)
        docRef.set(nuevoUsuario).await()
    }

    suspend fun actualizarUsuarioEnNegocio(negocioId: String, usuario: Usuario) {
        if (usuario.id.isNotEmpty()) {
            negociosCollection.document(negocioId).collection("usuarios").document(usuario.id).set(usuario).await()
        }
    }

    suspend fun crearNegocio(negocio: Negocio, adminUsuario: Usuario): String {
        val docRef = negociosCollection.document()
        val id = docRef.id
        val nuevoNegocio = negocio.copy(id = id)

        firestore.runBatch { batch ->
            batch.set(docRef, nuevoNegocio)
            val usuarioRef = docRef.collection("usuarios").document()
            batch.set(usuarioRef, adminUsuario.copy(id = usuarioRef.id))
        }.await()

        return id
    }

    suspend fun obtenerTodosLosNegocios(): List<Negocio> {
        val query = negociosCollection.get().await()
        return query.toObjects(Negocio::class.java)
    }

    suspend fun actualizarEstadoNegocio(negocioId: String, activo: Boolean) {
        if (negocioId.isNotEmpty()) {
            negociosCollection.document(negocioId).update("activo", activo).await()
        }
    }

    suspend fun actualizarNegocio(negocio: Negocio) {
        negociosCollection.document(negocio.id).set(negocio).await()
    }

    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
