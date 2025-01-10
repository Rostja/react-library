import { useOktaAuth } from '@okta/okta-react';
import { useEffect, useState } from 'react';
import MessageModel from '../../../models/MessageModel';
import { SpinnerLoading } from '../../Utils/SpinnerLoading';

export const AdminMessages = () => {

    const { authState} = useOktaAuth();

    //Normal Loading Pieces
    const [isLoadingMessages, setIsLoadingMessages] = useState(true);
    const [httpError, setHttpError] = useState(null);

    //Messages endpoint State
    const [messages, setMessages] = useState<MessageModel[]>([]);
    const [messagesPerPage] = useState(5);

    //Pagination
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        const fetchUserMessages = async () => {

        }
        fetchUserMessages().catch((error: any) => {
            setIsLoadingMessages(false);
            setHttpError(error.message);
        })
        window.scrollTo(0,0);
    }, [authState, currentPage]);

    if(isLoadingMessages) {
        return (
            <SpinnerLoading/>
        );
    }

    if(httpError) {
        return (
            <div className='container m-5'>
                <p>{httpError}</p>
            </div>
        );
    }
    const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

    return();
}