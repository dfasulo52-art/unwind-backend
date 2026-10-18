package com.unwind.project.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

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

@RestController
@RequestMapping("/api/prodotti")
@CrossOrigin(origins = "http://localhost:4200")
public class ProdottoController {

    @Autowired
    private ProdottoRepository prodottoRepository;

    // 1. METODO GET: Restituisce la lista di tutti i prodotti al frontend (fondamentale per mostrarli)
    @GetMapping
    public List<Prodotto> getTuttiIProdotti() {
        return prodottoRepository.findAll();
    }

    // 2. METODO POST: Crea un nuovo prodotto con eventuale foto
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Prodotto> creaProdotto(
            @RequestPart("prodotto") Prodotto prodotto,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {

        try {
            if (foto != null && !foto.isEmpty()) {
                String nomeFile = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename();
                Path percorsoGettato = Paths.get("uploads/" + nomeFile);
                Files.createDirectories(percorsoGettato.getParent());
                Files.copy(foto.getInputStream(), percorsoGettato, StandardCopyOption.REPLACE_EXISTING);
                
                prodotto.setFoto(nomeFile);
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
            if (foto != null && !foto.isEmpty()) {
                String nomeFile = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename();
                Path percorsoGettato = Paths.get("uploads/" + nomeFile);
                Files.createDirectories(percorsoGettato.getParent());
                Files.copy(foto.getInputStream(), percorsoGettato, StandardCopyOption.REPLACE_EXISTING);
                
                prodotto.setFoto(nomeFile);
            }
            
            Prodotto prodottoAggiornato = prodottoRepository.save(prodotto);
            return ResponseEntity.ok(prodottoAggiornato);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // 4. METODO DELETE: Elimina il prodotto corrispondente all'ID ricevuto da Angular
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminaProdotto(@PathVariable Long id) {
        if (!prodottoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        prodottoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}