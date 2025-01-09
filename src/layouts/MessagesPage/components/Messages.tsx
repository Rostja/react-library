import { useEffect, useState } from "react";
import { useOktaAuth } from "@okta/okta-react";
import { SpinnerLoading } from "../../Utils/SpinnerLoading";
import MessageModel from "../../../models/MessageModel";

export const Messages = () => {

    const { authState } = useOktaAuth();
    const [ isLoadingMessages, setIsLoadingMessages] = useState(true);
    const [ httpError, setHttpError ] = useState(null);

    //Messages
    const [messages, setMessages] = useState<MessageModel[]>([]);

    //Pagination
    const [messagesPerPage] = useState(5);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        const fetchUserMessages = async () => {
            if(authState && authState?.isAuthenticated) {
                const url = `http://localhost:8080/api/messages/search/findByUserEmail/?userEmail=${authState?.accessToken?.claims.sub}&page=${currentPage - 1}&size=${messagesPerPage}`;
                const requestOptions = {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
                        'Content-Type': 'application/json'
                    }
                };
                const messagesResponse = await fetch(url, requestOptions);
                if (!messagesResponse.ok) {
                    throw new Error('Something went wrong');
                }
                const messagesResponseJson = await messagesResponse.json();
                setMessages(messagesResponseJson._embedded.messages);
                setTotalPages(messagesResponseJson.page.totalPages);
            }
            setIsLoadingMessages(false);
    }
    fetchUserMessages().catch((error: any) => {
        setHttpError(error.message);
        setIsLoadingMessages(false);
    })
    window.scrollTo(0, 0);
}, [authState, currentPage]);

    if(isLoadingMessages) {
    return (
        <SpinnerLoading/>
        );
    }

    if (httpError) {
        return (
            <div className="container m-5">
                <p>{httpError}</p>
            </div>
        )
    }

    const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

    return ();
}