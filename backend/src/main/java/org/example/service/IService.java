package org.example.service;

import org.example.network.BackendClient;

public interface IService<T> {
    public void notifyClients(String notification);
    public void logoutClient(T backendClient);
    public void loginClient(T backendClient);
}
