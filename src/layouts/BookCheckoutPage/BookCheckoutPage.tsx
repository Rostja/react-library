import { useState } from "react";
import BookModel from "../../models/BookModel";

export const BookCheckoutPage = () => {

    const [book, setBook] = useState<BookModel>();
    const [isLoadingBook, setIsLoadingBook] = useState(true);
    const [httpError, setHttpError] = useState(null);

    const bookId = (window.location.pathname).split('/')[2];

    return(
        <div>
            <h3>Hi world!</h3>
        </div>
    );
}