
# Category Filtering Feature Update

## New Endpoint

### Get Categories by District

Returns a list of all unique food categories for a given district.

-   **URL:** `/api/restaurants/categories`
-   **Method:** `GET`
-   **Query Parameters:**
    -   `district` (required): The Korean name of the district.

**Example:**

```bash
curl "http://localhost:8080/api/restaurants/categories?district=상대동"
```

## Updated Endpoints

### Get Restaurants by District (with Category Filter)

Returns a list of restaurants from the database for a given district, now with an optional filter for food category.

-   **URL:** `/api/restaurants`
-   **Method:** `GET`
-   **Query Parameters:**
    -   `district` (required): The Korean name of the district.
    -   `category` (optional): The food category to filter by.

**Example:**

```bash
curl "http://localhost:8080/api/restaurants?district=상대동&category=한식"
```

### Get Random Restaurant (with Category Filter)

Returns a random restaurant from the database for a given district, now with an optional filter for food category.

-   **URL:** `/api/restaurants/random`
-   **Method:** `GET`
-   **Query Parameters:**
    -   `district` (required): The Korean name of the district.
    -   `category` (optional): The food category to filter by.

**Example:**

```bash
curl "http://localhost:8080/api/restaurants/random?district=상대동&category=한식"
```
