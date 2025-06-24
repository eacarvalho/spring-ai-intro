FROM ollama/ollama:latest AS builder

# Start Ollama server in background and wait for it to become ready
RUN nohup ollama serve > /tmp/ollama.log 2>&1 & \
    sleep 10 && \
    ollama pull llama3.1:8b

# Final image
FROM ollama/ollama:latest

# Copy preloaded model cache
COPY --from=builder /root/.ollama /root/.ollama

# Expose Ollama port
EXPOSE 11434

# Run the Ollama server normally
CMD ["serve"]