package com.pdm.esas.utils.response

import androidx.annotation.Keep
import com.google.firebase.auth.FirebaseAuthException
import javax.net.ssl.HttpsURLConnection

data class AuthErrorResponse(
    val status: Status = Status.UNKNOWN,
    val statusCode: Int = -1,
    val message: String = "Algo deu errado"
) {
    @Keep
    enum class Status(val code: Int, val description: String) {
        UNKNOWN(-100, "Erro desconhecido"),
        INVALID_EMAIL(-200, "Usuario nao encontrado"),
        TOO_MANY_REQUESTS(-204, "Muitas tentativas. Tente novamente mais tarde"),
        INVALID_CREDENTIAL(-203, "Credenciais inválidas. Verifique seus dados"),
        OPERATION_NOT_ALLOWED(-205, "Operação não permitida"),
        EMAIL_ALREADY_IN_USE(-206, "Email já está em uso"),
        HTTP_UNAUTHORIZED(HttpsURLConnection.HTTP_UNAUTHORIZED, "Acesso não autorizado"),
        HTTP_FORBIDDEN(HttpsURLConnection.HTTP_FORBIDDEN, "Acesso não autorizado");
    }

    companion object {
        fun fromException(exception: Throwable): AuthErrorResponse {
            return if (exception is FirebaseAuthException) {
                when (exception.errorCode) {
                    "ERROR_INVALID_CREDENTIAL" -> AuthErrorResponse(
                        Status.INVALID_CREDENTIAL, -203, Status.INVALID_CREDENTIAL.description
                    )
                    "ERROR_INVALID_EMAIL" -> AuthErrorResponse(
                        Status.INVALID_EMAIL, -200, Status.INVALID_EMAIL.description
                    )
                    "ERROR_EMAIL_ALREADY_IN_USE" -> AuthErrorResponse(
                        Status.EMAIL_ALREADY_IN_USE, -206, Status.EMAIL_ALREADY_IN_USE.description
                    )
                    "ERROR_TOO_MANY_REQUESTS" -> AuthErrorResponse(
                        Status.TOO_MANY_REQUESTS, -204, Status.TOO_MANY_REQUESTS.description
                    )
                    "ERROR_OPERATION_NOT_ALLOWED" -> AuthErrorResponse(
                        Status.OPERATION_NOT_ALLOWED, -205, Status.OPERATION_NOT_ALLOWED.description
                    )
                    else -> AuthErrorResponse(
                        Status.UNKNOWN, -100, exception.localizedMessage ?: Status.UNKNOWN.description
                    )
                }
            } else {
                AuthErrorResponse(
                    Status.UNKNOWN, -100, exception.localizedMessage ?: Status.UNKNOWN.description
                )
            }
        }
    }
}
