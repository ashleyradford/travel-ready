# Travel Ready
A hotel advisor application that hosts imported hotel data, user reviews, favorites, and more for securely logged in user accounts.

## Run
Make sure that the MySQL database specified in `database.properties` is up and running.<br>
Run `HotelServer.main()` class.

**Key hotel features include:**
- User Registration: users may register with unique usernames
- Password Strength: user passwords must satisfy a set of requirements (not too short or long, contains at least one lowercase letter, one uppercase letter, one special character, and a digit character). 
- Login and Logout: user maintained sessions
- Hotel Search: logged in users may search for a hotel by name
- Hotel Information: hotel pages that include hotel name and id, address, average rating, a link to that hotel's expedia page, and list of reviews
- Weather: weather data is fetched from meteo and displayed on each hotel's info page
- Add Review: logged in users may add up to one review per hotel (to prevent review bombing)
- Modify Review: logged in users may edit their own reviews
- Favorites: user may favorite hotels and view a list of their favorites
- Expedia History: users may check which hotels they visit through Expedia
- Last Login: users may check their latest login times

### Ajax Features
Review pagination, weather updates, and favorited hotels are updated via Ajax so that the sending and receiving of data from the database is done asynchronously and does not require a reload of the entire webpage.
