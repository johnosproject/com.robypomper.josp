package com.robypomper.discovery;


/**
 * Interface for Publisher implementations.
 * <p>
 * Each AbsPublisher instance represent a single service that can be published or
 * hide.
 */
public interface Publisher {

    // Publication mngm

    /**
     * @return <code>true</code> if the represented service's info are published.
     */
    boolean isPublished();

    /**
     * Publish represented service.
     *
     * @param waitForPublished stop current thread until the service is published.
     */
    void publish(boolean waitForPublished) throws PublishException;

    /**
     * De-publish represented service.
     *
     * @param waitForDepublished stop current thread until the service is de-published.
     */
    void hide(boolean waitForDepublished) throws PublishException;


    // Getters

    /**
     * @return the service name to publish.
     */
    String getServiceName();

    /**
     * @return the service type to publish.
     */
    String getServiceType();

    /**
     * @return the service port to publish.
     */
    int getServicePort();

    /**
     * @return the extra text related with service.
     */
    String getServiceExtraText();


    // Exceptions

    /**
     * Exceptions thrown on publication/hiding errors.
     */
    class PublishException extends Throwable {
        public PublishException(String msg) {
            super(msg);
        }

        public PublishException(String msg, Throwable e) {
            super(msg, e);
        }
    }

}