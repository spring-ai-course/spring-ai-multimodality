package com.javatechie.service;

import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class MultiModalityService {


    private final OpenAiAudioTranscriptionModel audioTranscriptionModel;

    private final OpenAiAudioSpeechModel audioSpeechModel;

    private final ImageModel imageModel;

    public MultiModalityService(OpenAiAudioTranscriptionModel audioTranscriptionModel,
                                OpenAiAudioSpeechModel audioSpeechModel,
                                ImageModel imageModel) {
        this.audioTranscriptionModel = audioTranscriptionModel;
        this.audioSpeechModel = audioSpeechModel;
        this.imageModel = imageModel;
    }

    // convert voice/ audio / speech -> text

    public String convertAudioToText(Resource audioFile){
        return audioTranscriptionModel.call(audioFile);
    }

    // convert Text -> Audio

    public String convertTextToAudio(String textInput) throws IOException {
        OpenAiAudioSpeechOptions options=OpenAiAudioSpeechOptions
                .builder()
                .voice(OpenAiAudioApi.SpeechRequest.Voice.SHIMMER)
                .build();
        byte[] audioContentBytes = audioSpeechModel
                .call(new TextToSpeechPrompt(textInput, options))
                .getResult().getOutput();
        Path audioPath= Path.of("ai_generated_audio.mp3");
        Files.write(audioPath, audioContentBytes);
        return "Audio generated and saved to: " + audioPath.toAbsolutePath();
    }

    // convert Text -> Image
    public String generateImageFromText(String input){
        ImageOptions options= OpenAiImageOptions.builder()
                .quality("hd")
                .style("natural")
                .height(1024)
                .width(1024)
                .user("basant")
                .responseFormat("url")
                .build();

         return imageModel.call(new ImagePrompt(input,options))
                 .getResult()
                 .getOutput()
                 .getUrl();
    }
}
