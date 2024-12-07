package com.pdm.esas.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.pdm.esas.R
import kotlinx.coroutines.launch

@Composable
fun LoginView(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit
) {
    val state = viewModel.state.value
    // #TODO CRIAR COMPONENTE DE SNABACKBAR MAIS INTELIGENTE E GENERICO, COM CORES QUE REFLETEM O A GRAVIDADE DO PROBLEMA
    // SUGESTAO É CRIAR UM BASEVIEW MODEL QUE PODE SER PASSADO PARA TODOS OS VIEW MODELS QUE VOU CRIAR
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.error) {
        state.error?.let { errorMessage ->
            scope.launch {
                snackbarHostState.showSnackbar(errorMessage)
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo), // Mudar para o logo correto
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 24.dp)
                )

                // Título da tela
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Campo de Email
                OutlinedTextField(
                    value = state.username,
                    onValueChange = { viewModel.onUsernameChange(it) },
                    label = { Text("Email") },
                    isError = state.emailError != null,
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = "Email",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )

                // Mensagem de erro do Email
                if (state.emailError != null) {
                    Text(
                        text = state.emailError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de Senha
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Senha") },
                    isError = state.passwordError != null,
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.onPasswordVisibilityChange() }) {
                            Icon(
                                imageVector = if (state.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (state.passwordVisible) "Ocultar senha" else "Mostrar senha",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )

                // Mensagem de erro da Senha
                if (state.passwordError != null) {
                    Text(
                        text = state.passwordError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botão de Login
                Button(
                    onClick = {
                        viewModel.login(onLoginSuccess)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !state.isLoading,
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .alpha(0.7f),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Entrar",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Texto para cadastro ou recuperação de senha
                TextButton(onClick = { /* Navegar para tela de cadastro ou recuperação */ }) {
                    Text(
                        text = "Esqueceu sua senha?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
