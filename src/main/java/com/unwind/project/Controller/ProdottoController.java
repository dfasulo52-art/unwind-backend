package com.unwind.project.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.unwind.project.Model.Prodotto;
import com.unwind.project.Repository.ProdottoRepository;
import com.unwind.project.Service.SupabaseStorageService; // Assicurati che il package corrisponda

@RestController
@RequestMapping("/api/prodotti")
@CrossOrigin(origins = "http://localhost:4200")
public class ProdottoController {

    @Autowired
    private ProdottoRepository prodottoRepository;

    @Autowired
    private SupabaseStorageService supabaseStorageService;

    // 1. METODO GET: Restituisce la lista di tutti i prodotti al frontend (fondamentale per mostrarli)
    @GetMapping
    public List<Prodotto> getTuttiIProdotti() {
        return prodottoRepository.findAll();
    }

    // 2. METODO POST: Crea un nuovo prodotto con eventuale foto su Supabase Storage
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Prodotto> creaProdotto(
            @RequestPart("prodotto") Prodotto prodotto,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {

        try {
            if (foto != null && !foto.isEmpty()) {
                // Carica l'immagine su Supabase Storage e ottiene l'URL pubblico completo
                String fileUrl = supabaseStorageService.uploadImage(foto);
                prodotto.setFoto(fileUrl);
            }

            Prodotto nuovoProdotto = prodottoRepository.save(prodotto);
            return ResponseEntity.ok(nuovoProdotto);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // 3. METODO PUT: Modifica un prodotto esistente
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Prodotto> modificaProdotto(
            @PathVariable Long id,
            @RequestPart("prodotto") Prodotto prodotto,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {

        if (!prodottoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        prodotto.setId(id);

        try {
            // Recupera il prodotto esistente per gestire la vecchia foto
            Prodotto prodottoEsistente = prodottoRepository.findById(id).orElse(null);

            if (foto != null && !foto.isEmpty()) {
                // 1. Carica la nuova immagine su Supabase Storage
                String fileUrl = supabaseStorageService.uploadImage(foto);
                prodotto.setFoto(fileUrl);

                // 2. Se c'era una vecchia foto su Supabase, eliminala per pulizia
                if (prodottoEsistente != null && prodottoEsistente.getFoto() != null) {
                    supabaseStorageService.deleteImage(prodottoEsistente.getFoto());
                }
            } else {
                // Se non viene caricata una nuova foto, manteniamo quella esistente
                if (prodottoEsistente != null && prodotto.getFoto() == null) {
                    prodotto.setFoto(prodottoEsistente.getFoto());
                }
            }
            
            Prodotto prodottoAggiornato = prodottoRepository.save(prodotto);
            return ResponseEntity.ok(prodottoAggiornato);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // 4. METODO DELETE: Elimina il prodotto e cancella la relativa foto da Supabase Storage
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminaProdotto(@PathVariable Long id) {
        Prodotto prodotto = prodottoRepository.findById(id).orElse(null);
        
        if (prodotto == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 1. Se il prodotto ha una foto associata, eliminala dal bucket di Supabase
        if (prodotto.getFoto() != null && !prodotto.getFoto().isEmpty()) {
            supabaseStorageService.deleteImage(prodotto.getFoto());
        }
        
        // 2. Elimina il record dal database
        prodottoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}