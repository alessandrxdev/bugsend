# BugSend

Una librería sencilla para detectar los errores de su aplicación y enviarlos por correo.

<p align="center">

[![](https://jitpack.io/v/applifycu/bugsend.svg)](https://jitpack.io/#applifycu/bugsend)

</p>

## Agregar

1. Agrega a la raíz de su proyecto

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
		}
	}
```
    
2. Agregar la dependencia a su proyecto
    
```groovy
    dependencies {
	    implementation 'com.github.applifycu:bugsend:1.0.1'
	}
```
    
## Uso

En su `MyApplication.class` agrege

```java
public class MyApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    
    Thread.setDefaultUncaughtExceptionHandler(new HandlerUtil(this));
    }
}
```

En su MainActivity.class agregue

```java
   
 new BugSend(this)
     .setTitle(getString(R.string.title_dialog))
     .setIcon(R.drawable.ic_bug_report_24px)
     .setMessage(getString(R.string.message_dialog))
     .setEmail("soporte@email.com")
     .setAsunto("REPORT/APP")
     .setExtraInfo("EXTRA MESSAGE") // aqui puedes agregar un texto adicional como la versión de la app.
     .show();
 ```
 
 Su AndroidManifest
 
 ```xml
  <application
    android:name=".MyApplication"
```

### Contacto

Para dudas o sugerencias puede ponerse en contacto con nosotros en:

soporteapplify@gmail.com