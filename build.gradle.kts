// Top-level build file where you can add configuration options common to all sub-projects/modules.
//plugins {
  //  alias(libs.plugins.android.application) apply false
    //id("com.google.gms.google-services") version "4.4.0"
    //id("com.google.gms.google-services") version "4.4.2" apply false
    //id("com.google.firebase.crashlytics") version "2.9.9"
//}

plugins {
    id("com.android.application") version "8.1.0" apply false
    id("com.android.library") version "8.1.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false

}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}



