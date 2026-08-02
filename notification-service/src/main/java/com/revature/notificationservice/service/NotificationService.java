package com.revature.notificationservice.service;

import com.revature.notificationservice.dto.SendNotificationRequest;
import com.revature.notificationservice.entity.Notification;
import com.revature.notificationservice.repository.NotificationRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final RestTemplate restTemplate;
    private final String fromEmail;

    public NotificationService(NotificationRepository notificationRepository, 
                               JavaMailSender mailSender, 
                               RestTemplate restTemplate,
                               @Value("${spring.mail.username}") String fromEmail) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
        this.restTemplate = restTemplate;
        this.fromEmail = fromEmail;
    }

    public Notification sendNotification(SendNotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        if (request.getReferenceId() != null) {
            // Map referenceId to relatedId
            try {
                notification.setRelatedId(Long.parseLong(request.getReferenceId()));
            } catch (NumberFormatException e) {
                // Ignore if not a valid Long
            }
        }
        notification.setIsRead(false);

        Notification saved = notificationRepository.save(notification);
        sendEmail(saved.getUserId(), saved.getTitle(), saved.getMessage());
        return saved;
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false);
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId,
                false);
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    // Admin methods
    public List<Notification> getAllNotifications(int page, int size) {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getNotificationsByType(String type) {
        return notificationRepository.findByType(type);
    }

    public Notification createNotification(Long userId, String title, String message, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);

        Notification saved = notificationRepository.save(notification);
        sendEmail(saved.getUserId(), saved.getTitle(), saved.getMessage());
        return saved;
    }

    private void sendEmail(Long userId, String subject, String body) {
        if (userId == null) {
            return;
        }
        try {
            String userServiceUrl = "http://user-service/api/auth/profile/" + userId;
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = restTemplate.getForObject(userServiceUrl, Map.class);
            
            if (responseMap != null && responseMap.containsKey("user")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> user = (Map<String, Object>) responseMap.get("user");
                String email = (String) user.get("email");
                
                if (email != null && !email.trim().isEmpty()) {
                    jakarta.mail.internet.MimeMessage mimeMessage = mailSender.createMimeMessage();
                    org.springframework.mail.javamail.MimeMessageHelper helper = 
                            new org.springframework.mail.javamail.MimeMessageHelper(mimeMessage, "utf-8");
                    
                    String htmlMsg = "<!DOCTYPE html><html><head><style>"
                            + "body { font-family: 'Inter', Helvetica, Arial, sans-serif; background-color: #f4f6f8; margin: 0; padding: 0; color: #333333; }"
                            + ".email-container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); overflow: hidden; border: 1px solid #e1e8ed; }"
                            + ".email-header { background: linear-gradient(135deg, #E65100, #F57C00); padding: 30px; text-align: center; color: #ffffff; }"
                            + ".email-header h1 { margin: 0; font-size: 28px; font-weight: 700; letter-spacing: -0.5px; }"
                            + ".email-body { padding: 40px 30px; line-height: 1.6; }"
                            + ".email-body h2 { color: #E65100; font-size: 20px; margin-top: 0; font-weight: 600; }"
                            + ".message-card { background-color: #f8f9fa; border-left: 4px solid #FF5722; padding: 20px; border-radius: 0 8px 8px 0; margin: 20px 0; font-size: 16px; color: #495057; }"
                            + ".cta-button { display: inline-block; background-color: #E65100; color: #ffffff !important; text-decoration: none; padding: 12px 28px; border-radius: 6px; font-weight: 600; margin-top: 10px; }"
                            + ".email-footer { background-color: #f8f9fa; padding: 20px; text-align: center; font-size: 12px; color: #868e96; border-top: 1px solid #e9ecef; }"
                            + "</style></head><body>"
                            + "<div class='email-container'>"
                            + "<div class='email-header'><h1>RevCart</h1></div>"
                            + "<div class='email-body'>"
                            + "<h2>" + subject + "</h2>"
                            + "<div class='message-card'>" + body + "</div>"
                            + "<p>If you have any questions or need support, feel free to visit your account profile or contact our helpdesk.</p>"
                            + "<a href='http://localhost:8080' class='cta-button'>Go to Dashboard</a>"
                            + "</div>"
                            + "<div class='email-footer'>"
                            + "<p>&copy; 2026 RevCart Delivery. All rights reserved.</p>"
                            + "<p>Delivering Freshness to Your Doorstep.</p>"
                            + "</div>"
                            + "</div></body></html>";
                    
                    helper.setText(htmlMsg, true);
                    helper.setTo(email);
                    helper.setSubject(subject);
                    helper.setFrom(fromEmail);
                    mailSender.send(mimeMessage);
                }
            }
        } catch (Exception e) {
            // Fallback: log the warning but don't break transactions
            System.err.println("Failed to send email to user " + userId + ": " + e.getMessage());
        }
    }
}
