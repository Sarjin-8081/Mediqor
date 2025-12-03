package com.mediqor.app.ui.screens.auth
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediqor.app.R
import com.mediqor.app.ui.theme.MintGreen

import com.mediqor.app.ui.theme.PurpleGrey80



class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegistrationBody()
        }
    }
}

@Composable
fun RegistrationBody() {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }
    var terms by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as Activity

    val sharedPreference = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val editor = sharedPreference.edit()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
        {
            Spacer(modifier = Modifier.height(50.dp))

            Text(
                "Sign Up",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                "Enter your email",
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            CharacterNew(
                value = email,
                onValueChange = { email = it },
                label = "Username",

            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Enter your password",
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                trailingIcon = {
                    IconButton(onClick = { visibility = !visibility }) {
                        Icon(
                            painter = painterResource(
                                if (visibility)
                                    R.drawable.baseline_visibility_24
                                else
                                    R.drawable.baseline_visibility_off_24
                            ),
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (visibility)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                shape = RoundedCornerShape(15.dp),
                placeholder = { Text("***") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MintGreen,
                    unfocusedContainerColor = PurpleGrey80,

                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = terms,
                    onCheckedChange = { terms = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Blue,
                        checkmarkColor = Color.White
                    )
                )
                Text("I agree to the terms & conditions")
            }

            Button(
                onClick = {

                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!terms) {
                        Toast.makeText(context, "Please agree to terms & conditions", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    editor.putString("email", email)
                    editor.putString("password", password)
                    editor.apply()

                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                    activity.finish()

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
                    .height(60.dp),
                elevation = ButtonDefaults.buttonElevation(15.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MintGreen,)



            ) {
                Text("Sign up")
            }

            Text(
                buildAnnotatedString {
                    append("Already have account? ")
                    withStyle(SpanStyle(color = Color.Blue)) {
                        append("LOGIN")
                    }
                },
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
            )

            Spacer(modifier = Modifier.height(210.dp))


        }
    }
}

@Composable
fun CharacterNew(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        shape = RoundedCornerShape(15.dp),

        colors = TextFieldDefaults.colors(

            focusedContainerColor = MintGreen,
            unfocusedContainerColor = PurpleGrey80,
            unfocusedIndicatorColor = Color.Transparent,



            )
    )
}

@Preview
@Composable
fun RegistrationBodyPreview() {
    RegistrationBody()
}