#!/bin/bash

# Script for the docker container to start ollama and pull the model
# This is only used by the docker-compose-ollama.yml file
# =====

# Start Ollama in the background.
/bin/ollama serve &
# Record Process ID.
pid=$!

# Pause for Ollama to start.
sleep 5

echo "🔴 Retrieve llama3.2 model..."
ollama pull llama3.2
echo "🟢 Done!"

# Wait for Ollama process to finish.
wait $pid
