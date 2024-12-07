package com.pdm.esas.utils.response

import androidx.annotation.Keep
import com.google.firebase.auth.FirebaseAuthException
import javax.net.ssl.HttpsURLConnection


// #TODO GENERALIZAR ESSA CLASSE PARA TODOS OS ERROS RELACIONADOS AO FIREBASE, PARA FACILITAR O TOAST E O LOG
data class AuthErrorResponse(
    val status: Status = Status.UNKNOWN,
    val statusCode: Int = -1,
    val message: String = "Algo deu errado"
) {
    @Keep
    enum class Status(val code: Int, val description: String) {
        UNKNOWN(-100, "Erro desconhecido"),
        INVALID_EMAIL(-200, "Usuario nao encontrado"),
        USER_DISABLED(-201, "Credenciais inválidas. Verifique seus dados"),
        USER_NOT_FOUND(-202, "Usuário não encontrado"),
        WRONG_PASSWORD(-203, "Senha incorreta"),
        TOO_MANY_REQUESTS(-204, "Muitas tentativas. Tente novamente mais tarde"),
        INVALID_CREDENTIAL(-203, "Credenciais inválidas. Verifique seus dados"),
        OPERATION_NOT_ALLOWED(-205, "Operação não permitida"),
        EMAIL_ALREADY_IN_USE(-206, "Email já está em uso"),
        WEAK_PASSWORD(-207, "Senha muito fraca. Escolha uma mais forte"),
        NETWORK_ERROR(-102, "Erro de conexão com a rede"),
        HTTP_UNAUTHORIZED(HttpsURLConnection.HTTP_UNAUTHORIZED, "Não autorizado"),
        HTTP_FORBIDDEN(HttpsURLConnection.HTTP_FORBIDDEN, "Acesso proibido");

        companion object {
            private val codes = entries.toTypedArray()
            operator fun get(code: Int) = codes.firstOrNull { it.code == code } ?: UNKNOWN
        }
    }

    companion object {
        fun fromException(exception: Exception): AuthErrorResponse {
            return when (exception) {
                is FirebaseAuthException -> {
                    when (exception.errorCode) {
                        "ERROR_INVALID_CREDENTIAL" -> AuthErrorResponse(
                            Status.INVALID_CREDENTIAL, -208, Status.INVALID_CREDENTIAL.description
                        )
                        "ERROR_INVALID_EMAIL" -> AuthErrorResponse(
                            Status.INVALID_EMAIL, -200, Status.INVALID_EMAIL.description
                        )
                        "ERROR_USER_DISABLED" -> AuthErrorResponse(
                            Status.USER_DISABLED, -201, Status.USER_DISABLED.description
                        )
                        "ERROR_USER_NOT_FOUND" -> AuthErrorResponse(
                            Status.USER_NOT_FOUND, -202, Status.USER_NOT_FOUND.description
                        )
                        "ERROR_WRONG_PASSWORD" -> AuthErrorResponse(
                            Status.WRONG_PASSWORD, -203, Status.WRONG_PASSWORD.description
                        )
                        "ERROR_EMAIL_ALREADY_IN_USE" -> AuthErrorResponse(
                            Status.EMAIL_ALREADY_IN_USE, -206, Status.EMAIL_ALREADY_IN_USE.description
                        )
                        "ERROR_WEAK_PASSWORD" -> AuthErrorResponse(
                            Status.WEAK_PASSWORD, -207, Status.WEAK_PASSWORD.description
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
                }
                else -> AuthErrorResponse(
                    Status.UNKNOWN, -100, exception.localizedMessage ?: Status.UNKNOWN.description
                )
            }
        }
    }
}
