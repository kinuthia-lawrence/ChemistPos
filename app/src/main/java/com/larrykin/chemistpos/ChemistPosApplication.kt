package com.larrykin.chemistpos

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp //this triggers the generation of the Hilt components
class ChemistPosApplication :Application()