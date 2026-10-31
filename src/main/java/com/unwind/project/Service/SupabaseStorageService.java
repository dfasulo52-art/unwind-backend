package com.unwind.project.Service; // Sostituisci con il tuo package reale

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    public String uploadImage(MultipartFile file) throws IOException {
        // Genera un nome unico per evitare conflitti o problemi con caratteri speciali/spazi
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "_" + (originalFilename != null ? originalFilename.replaceAll("\\s+", "_") : "image.jpg");
        
        // URL delle API REST di Supabase Storage per il caricamento degli oggetti
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        
        // Aggiungiamo sia l'header 'apikey' che 'Authorization' richiesti dalle nuove chiavi Supabase
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);
        
        // Imposta dinamicamente il Content-Type reale del file (es. image/jpeg, image/png)
        String contentType = file.getContentType();
        if (contentType != null && !contentType.isEmpty()) {
            headers.setContentType(MediaType.parseMediaType(contentType));
        } else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        // Invia la richiesta POST a Supabase
        ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            // Restituisce l'URL pubblico definitivo dell'immagine da salvare nel database
            return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;
        } else {
            throw new RuntimeException("Errore durante l'upload dell'immagine su Supabase Storage");
        }
    }
}