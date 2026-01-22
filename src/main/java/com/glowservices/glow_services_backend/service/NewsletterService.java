package com.glowservices.glow_services_backend.service;

import com.glowservices.glow_services_backend.model.entity.Newsletter;
import com.glowservices.glow_services_backend.repository.NewsletterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsletterService {

    private final NewsletterRepository newsletterRepository;

    public Newsletter subscribe(String email) {
        if (newsletterRepository.existsByEmail(email)) {
            // Already subscribed, maybe reactivate if inactive, but for now just return existing
            // or throw exception depending on requirements. 
            // The prompt says "save in database", so if it exists, maybe just ignore or update.
            // Let's assume we just return it or throw if strictly new.
            // But for user experience, better not error if they are just re-entering.
            return newsletterRepository.findByEmail(email).get();
        }
        Newsletter newsletter = new Newsletter();
        newsletter.setEmail(email);
        return newsletterRepository.save(newsletter);
    }
}
