// ResultProfileActivity.kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sportsstore.databinding.FragmentProfileBinding
import com.example.sportsstore.models.ChildItem
import com.example.sportsstore.models.ParentItem

class FragmentProfileBinding : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)




        return binding.root
    }
}