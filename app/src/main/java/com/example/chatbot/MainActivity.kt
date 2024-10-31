package com.example.chatbot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chatbot.databinding.ActivityMainBinding
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Variables to hold prompt and result text
    private var promptText: String = ""
    private var resultText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Restore saved instance state if available
        if (savedInstanceState != null) {
            promptText = savedInstanceState.getString("PROMPT_TEXT", "")
            resultText = savedInstanceState.getString("RESULT_TEXT", "")
            binding.eTPrompt.setText(promptText)
            binding.tVResult.text = resultText
        }

        binding.btnSubmit.setOnClickListener {
            val prompt = binding.eTPrompt.text.toString().trim()
            val apiKey = "AIzaSyB82aYOta-cUvcWqixFjIrtE1rzBEU1WKE"

            if (prompt.isEmpty()) {
                binding.tVResult.text = "Please enter a prompt."
                return@setOnClickListener
            }

            binding.tVResult.append("\nYou: $prompt\n") // Append new prompt
            binding.tVResult.append("\nLoading...\n") // Show loading message

            binding.eTPrompt.text.clear()
            promptText = ""

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-pro",
                        apiKey = apiKey
                    )
                    val response = generativeModel.generateContent(prompt)

                    withContext(Dispatchers.Main) {
                        // Remove the loading message and append the bot's response
                        binding.tVResult.text =
                            binding.tVResult.text.toString().replace("Loading...\n", "")
                        // Append response
                        resultText = "Bot: ${response.text ?: "No response text available."}\n"
                        binding.tVResult.append(resultText)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        // Remove the loading message and append the error message
                        binding.tVResult.text =
                            binding.tVResult.text.toString().replace("Loading...\n", "")
                        // Append error message
                        resultText = "Error: ${e.message}\n"
                        binding.tVResult.append(resultText)
                    }
                }
            }
        }
    }

    // Save the state
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("PROMPT_TEXT", binding.eTPrompt.text.toString())
        outState.putString("RESULT_TEXT", binding.tVResult.text.toString())
    }
}
