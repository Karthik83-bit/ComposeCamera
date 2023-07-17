package com.example.composecamera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composecamera.ui.theme.ComposeCameraTheme
import java.security.Permission
import java.util.jar.Pack200

interface PermissionTextProvider {
 fun getText(isPermanentlyDeclined:Boolean):String
}
class CameraPermissionTextProvider:PermissionTextProvider{
    override fun getText(isPermanentlyDeclined: Boolean) =
        if(isPermanentlyDeclined){
            "It seems you have permanently declined Grant Permission by going to App settings"
        }else{
            "This is a required Permission Please Grant Permisssion to continue"
        }



}

class MainActivity : ComponentActivity() {
    lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel=ViewModelProvider(this)[MainViewModel::class.java]


        setContent {

            val dialogPermList =viewModel.permissionVisibleDialog
            val cameraPermission= rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), onResult = { isGranted->
            viewModel.addPermission(isGranted,Manifest.permission.CAMERA)
            })
            LaunchedEffect(key1 = true){
              cameraPermission.launch(Manifest.permission.CAMERA)
            }
            ComposeCameraTheme {
                Text("CamerAPP")
                dialogPermList.forEach{perm->
                    showDialog(
                        isPermanentlyDeclined = !shouldShowRequestPermissionRationale(perm),
                        textProvider =CameraPermissionTextProvider() ,
                        onDismiss = {
                                    viewModel.removeFromQueue()
                        },
                        onOkClick = {
                            viewModel.removeFromQueue()
                            cameraPermission.launch(Manifest.permission.CAMERA)
                        },
                        goToApp = {
                                  intentToAppSetting()
                                  },
                        modifier =Modifier
                            .height(IntrinsicSize.Max)
                            .fillMaxWidth()
                            .background(White, RoundedCornerShape(10.dp))
                            .padding(10.dp)

                    )
                }

            }
        }
    }

    private fun intentToAppSetting() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",packageName,null)).also(::startActivity)
    }


    @Composable
    private fun HandleDialog(dialogPermList: MutableList<String>) {

    }

    private @Composable
    fun showDialog(
        isPermanentlyDeclined:Boolean,
        textProvider:PermissionTextProvider,

        onDismiss:()->Unit,
        onOkClick:()->Unit,
        goToApp:()->Unit,
        modifier:Modifier


    ) {
        Dialog(onDismissRequest = {
                                  onDismiss()
        },) {
            Column(modifier ){
                Text(
                   text="Grant permission"

                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text=textProvider.getText(isPermanentlyDeclined = isPermanentlyDeclined)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Divider(thickness = 2.dp)
                Text(text=
                    if(isPermanentlyDeclined){
                        "Grant Permisssion"
                    }else{
                        "OK"
                    },
                    textAlign = TextAlign.Center,
                    fontWeight= FontWeight.Bold,
                    fontSize = 20.sp,

                    modifier = Modifier.clickable {
                        if(isPermanentlyDeclined){
                            goToApp()
                        }else{
                            onOkClick()
                        }
                    }.padding(16.dp)
                        .fillMaxWidth()
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
            if(viewModel.permissionVisibleDialog.size!=0){
                viewModel.removeFromQueue()
            }

        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeCameraTheme {
        Greeting("Android")
    }
}
