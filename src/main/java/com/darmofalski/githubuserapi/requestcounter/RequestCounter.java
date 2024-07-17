package com.darmofalski.githubuserapi.requestcounter;

import jakarta.persistence.*;


@Entity
@Table(name = "REQUEST_COUNTER")
public class RequestCounter {

    public RequestCounter() {
    }

    public RequestCounter(String login, Long requestCount) {
        this.login = login;
        this.requestCount = requestCount;
    }

    @Id
    @Column(name = "LOGIN")
    private String login;

    @Column(name = "REQUEST_COUNT")
    private Long requestCount;

    public Long getRequestCount() {
        return requestCount;
    }

    public void incrementRequestCount() {
        this.requestCount++;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestCounter that = (RequestCounter) o;

        if (!login.equals(that.login)) return false;
        return requestCount.equals(that.requestCount);
    }

    @Override
    public int hashCode() {
        int result = login.hashCode();
        result = 31 * result + requestCount.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RequestCounter{" +
                "login='" + login + '\'' +
                ", requestCount=" + requestCount +
                '}';
    }
}
