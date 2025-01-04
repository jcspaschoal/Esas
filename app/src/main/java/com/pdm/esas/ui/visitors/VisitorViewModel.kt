package com.pdm.esas.ui.visitors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.esas.data.local.memory.InMemoryUserInfo
import com.pdm.esas.data.models.Visitor
import com.pdm.esas.data.repository.VisitorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VisitorState(
    val successMessage: String? = null,
    val errorMessage: String? = null
)


@HiltViewModel
class VisitorViewModel @Inject constructor(
        private val repository: VisitorRepository,
        private val inMemoryUserInfo: InMemoryUserInfo
    ) : ViewModel() {

    val nationalities = listOf(
        "Afghan", "Albanian", "Algerian", "American", "Andorran", "Angolan", "Antiguan", "Argentine",
        "Armenian", "Australian", "Austrian", "Azerbaijani", "Bahamian", "Bahraini", "Bangladeshi",
        "Barbadian", "Belarusian", "Belgian", "Belizean", "Beninese", "Bhutanese", "Bolivian", "Bosnian",
        "Botswanan", "Brazilian", "Bruneian", "Bulgarian", "Burkinabé", "Burmese", "Burundian", "Cabo Verdean",
        "Cambodian", "Cameroonian", "Canadian", "Central African", "Chadian", "Chilean", "Chinese",
        "Colombian", "Comoran", "Congolese (Congo-Brazzaville)", "Congolese (Congo-Kinshasa)", "Costa Rican",
        "Croatian", "Cuban", "Cypriot", "Czech", "Danish", "Djiboutian", "Dominican (Dominica)",
        "Dominican (Dominican Republic)", "Ecuadorean", "Egyptian", "Salvadoran", "Equatorial Guinean",
        "Eritrean", "Estonian", "Eswatini", "Ethiopian", "Fijian", "Finnish", "French", "Gabonese",
        "Gambian", "Georgian", "German", "Ghanaian", "Greek", "Grenadian", "Guatemalan", "Guinean",
        "Bissau-Guinean", "Guyanese", "Haitian", "Honduran", "Hungarian", "Icelander", "Indian",
        "Indonesian", "Iranian", "Iraqi", "Irish", "Israeli", "Italian", "Ivorian", "Jamaican",
        "Japanese", "Jordanian", "Kazakh", "Kenyan", "Kiribati", "Korean (North)", "Korean (South)",
        "Kosovar", "Kuwaiti", "Kyrgyz", "Laotian", "Latvian", "Lebanese", "Basotho", "Liberian",
        "Libyan", "Liechtensteiner", "Lithuanian", "Luxembourgish", "Malagasy", "Malawian", "Malaysian",
        "Maldivian", "Malian", "Maltese", "Marshallese", "Mauritanian", "Mauritian", "Mexican",
        "Micronesian", "Moldovan", "Monégasque", "Mongolian", "Montenegrin", "Moroccan", "Mozambican",
        "Namibian", "Nauruan", "Nepali", "New Zealander", "Nicaraguan", "Nigerien", "Nigerian",
        "North Macedonian", "Norwegian", "Omani", "Pakistani", "Palauan", "Palestinian", "Panamanian",
        "Papua New Guinean", "Paraguayan", "Peruvian", "Polish", "Portuguese", "Qatari",
        "Romanian", "Russian", "Rwandan", "Saint Kitts and Nevis", "Saint Lucian",
        "Saint Vincentian", "Samoan", "San Marinese", "Sao Tomean", "Saudi", "Senegalese",
        "Serbian", "Seychellois", "Sierra Leonean", "Singaporean", "Slovak", "Slovene", "Solomon Islander",
        "Somali", "South African", "South Sudanese", "Spanish", "Sri Lankan", "Sudanese",
        "Surinamese", "Swedish", "Swiss", "Syrian", "Tajik", "Tanzanian", "Thai", "Timorese",
        "Togolese", "Tongan", "Trinidadian", "Tunisian", "Turkish", "Turkmen", "Tuvaluan",
        "Ugandan", "Ukrainian", "Uruguayan", "Uzbek", "Vanuatuan", "Venezuelan", "Vietnamese",
        "Yemeni", "Zambian", "Zimbabwean"
    )


    val userId: String? = inMemoryUserInfo.getUserId()

        private val _state = MutableStateFlow(VisitorState())
        val state: StateFlow<VisitorState> = _state

        fun createVisitor(visitor: Visitor) {
            viewModelScope.launch {
                try {
                    repository.createVisitor(visitor)
                    _state.value = _state.value.copy(successMessage = "Visitante criado com successo")
                } catch (e: Exception) {
                    _state.value = _state.value.copy(errorMessage = "Não foi possível realizar essa ação no momento, tente novamente em instantes")
                }
            }
        }
    }
