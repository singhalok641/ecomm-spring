package com.example.ecomm.services;

import com.example.ecomm.clients.GroqClient;
import com.example.ecomm.dtos.GroqChatResponseDto;
import com.example.ecomm.models.Category;
import com.example.ecomm.models.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PitchServiceTest {
    @Mock
    private GroqClient groqClient;

    @InjectMocks
    private PitchService pitchService;

    @Test
    void generatePitch_groqAnswers_returnsTheTrimmedText() {
        when(groqClient.chat((anyMap()))).thenReturn(replyWith(" Glow up for $9.99! "));
        String pitch = pitchService.generatePitch(mascara());
        assertThat(pitch).isEqualTo("Glow up for $9.99!");
    }

    @Test
    void generatePitch_groqFails_returnsTheTemplateSentence() {
        when(groqClient.chat((anyMap()))).thenThrow(new RuntimeException("Grop is not available"));
        String pitch = pitchService.generatePitch(mascara());
        assertThat(pitch).isEqualTo("New arrival: Mascara for just $9.99. Check it out!");
    }

    private GroqChatResponseDto replyWith(String text) {
        GroqChatResponseDto.Message message = new GroqChatResponseDto.Message();
        message.setContent(text);
        GroqChatResponseDto.Choice choice = new GroqChatResponseDto.Choice();
        choice.setMessage(message);
        GroqChatResponseDto response = new GroqChatResponseDto();
        response.setChoices(List.of(choice));
        return response;
    }

    private Product mascara() {
        Category category = new Category();
        category.setName("beauty");
        Product product = new Product();
        product.setName("Mascara");
        product.setPrice(new BigDecimal("9.99"));
        product.setCategory(category);
        return product;
    }

}
