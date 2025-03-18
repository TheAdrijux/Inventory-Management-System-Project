# Inventory Management System Documentation

## 1. System Architecture

The Inventory Management System follows a layered architecture pattern [1], consisting of:

- **Presentation Layer**: JavaFX-based GUI components
- **Business Logic Layer**: Domain models and business rules
- **Data Access Layer**: JPA/Hibernate persistence layer

### 1.1 Design Patterns

The system implements several design patterns [2] to ensure maintainability and extensibility:

1. **Model-View-Controller (MVC)**
   - Models: Product, Category, Order, Client
   - Views: FXML-based UI layouts
   - Controllers: UI classes managing user interaction

2. **Observer Pattern**
   - Used in JavaFX properties for reactive UI updates
   - Enables loose coupling between components [3]

3. **Data Access Object (DAO)**
   - Abstracts database operations
   - Provides clean separation of concerns [4]

4. **Factory Pattern**
   - Used for creating entity manager instances
   - Ensures consistent object creation [2]

## 2. Domain Model

The system's domain model follows Object-Oriented Design principles [5]:

### 2.1 Entity Relationships
- One-to-Many: Category → Products
- One-to-Many: Client → Orders
- One-to-Many: Order → OrderItems
- Many-to-One: Product → Category

### 2.2 Business Rules
- Products must have non-negative quantities
- Orders must have at least one item
- Prices must be non-negative
- Each order must be associated with a client

## 3. Testing Strategy

The testing approach follows the Testing Pyramid concept [6]:

1. **Unit Tests**
   - Model classes (Product, Category, Order, Client)
   - Business logic validation
   - Entity relationships

2. **Integration Tests**
   - Database operations
   - Transaction management
   - Entity persistence

3. **UI Tests**
   - Component rendering
   - User interaction flows
   - Data binding

## 4. User Interface Design

The UI design follows established HCI principles [7]:

1. **Consistency**
   - Uniform layout across all windows
   - Consistent button placement
   - Standard color scheme

2. **Feedback**
   - Immediate response to user actions
   - Clear error messages
   - Status updates

3. **Error Prevention**
   - Input validation
   - Confirmation dialogs
   - Clear action labels

## 5. Data Management

### 5.1 Persistence Strategy
- JPA/Hibernate for ORM [8]
- Transaction management
- Connection pooling

### 5.2 Data Validation
- Bean Validation annotations
- Custom validation rules
- Database constraints

## References

[1] Buschmann, F., Meunier, R., Rohnert, H., Sommerlad, P., & Stal, M. (1996). Pattern-Oriented Software Architecture: A System of Patterns. John Wiley & Sons.

[2] Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.

[3] Freeman, E., Robson, E., Bates, B., & Sierra, K. (2004). Head First Design Patterns. O'Reilly Media.

[4] Fowler, M. (2002). Patterns of Enterprise Application Architecture. Addison-Wesley.

[5] Martin, R. C. (2017). Clean Architecture: A Craftsman's Guide to Software Structure and Design. Prentice Hall.

[6] Cohn, M. (2009). Succeeding with Agile: Software Development Using Scrum. Addison-Wesley.

[7] Nielsen, J. (1993). Usability Engineering. Morgan Kaufmann.

[8] Bauer, C., & King, G. (2015). Java Persistence with Hibernate. Manning Publications. 