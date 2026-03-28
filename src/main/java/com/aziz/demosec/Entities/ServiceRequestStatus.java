package com.aziz.demosec.Entities;

public enum ServiceRequestStatus {
    PENDING,       // créée, en attente d'assignation
    ACCEPTED,      // prestataire assigné et confirmé
    IN_PROGRESS,   // le prestataire est en route / sur place
    COMPLETED,     // service terminé
    CANCELLED      // annulée
}
