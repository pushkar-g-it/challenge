Before deploying APIs into the production below points should also be considered:
 1. A security service module should be added to secure the endpoints, Spring Security can be used for this.
 2. Spring boot Actuator can also be integrated to monitor the app.
 3. In memory data store should be replaced by a database. Spring Data JPA can be integrated to interact with the database.
 4. A separate DTO class can be used instead of domain class while interacting with the persistence layer. 
    As the api may get enhanced with new fields in the future releases & then we may want to store only relevant fields in the database as per client's need.
 5. Spring AOP can be implemented for a centralized exception handling mechanism.
 6. Internationalization can be configured to show messages based on user's locale.
 7. For containerization of the app we can add plugins such fabric8 plugin for Docker integration.
 8. Separate maven projects for accounts & transfer apis can be created for independent scalability & deployment,
    then openFiegn or restTemplate can be used for communication.