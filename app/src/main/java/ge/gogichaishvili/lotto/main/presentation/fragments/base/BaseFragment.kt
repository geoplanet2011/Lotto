package ge.gogichaishvili.lotto.main.presentation.fragments.base

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import ge.gogichaishvili.lotto.main.presentation.viewmodels.base.BaseViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.ParametersDefinition
import kotlin.reflect.KClass

open class BaseFragment<VM : BaseViewModel>(clazz: KClass<VM>) : Fragment() {

    protected val mViewModel: VM by if (isGlobal()) sharedViewModel(
        clazz = clazz,
        parameters = getParams(),
    ) else viewModel(
        clazz = clazz,
        parameters = getParams()
    )

    open fun bindLifeCycleToViewModel() = false

    open fun isGlobal() = false

    open fun requiresConnection() = false

    open fun getParams(): ParametersDefinition? = null

    open fun bindObservers() {}

    open fun bindViewActionListeners() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (bindLifeCycleToViewModel()) {
            if (mViewModel is LifecycleObserver)
                lifecycle.addObserver(mViewModel as LifecycleObserver)
            else
                throw Exception("View model must implement LifecycleObserver interface")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewActionListeners()
        bindObservers()
    }

    protected fun hasLocationPermission(): Boolean {
        val res = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return res == PackageManager.PERMISSION_GRANTED
    }

    protected fun hasCameraPermission(): Boolean {
        val res =
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        return res == PackageManager.PERMISSION_GRANTED
    }

    protected fun requestCameraPermission(requestCode: Int) {
        requestPermissions(
            arrayOf(Manifest.permission.CAMERA),
            requestCode
        )
    }

    protected fun hasReadExternalStoragePermission(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    protected fun hasWriteExternalStorage(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    protected fun requestStoragePermission(requestCode: Int) {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            requestCode
        )
    }
}