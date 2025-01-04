import { useOktaAuth } from "@okta/okta-react";
import ShelfCurrentLoans from "../../../models/ShelfCurrentLoans";
import { useEffect, useState } from "react";
import { SpinnerLoading } from "../../Utils/SpinnerLoading";


export const Loans = () => {

    const { authState } = useOktaAuth();
    const [httpError, setHttpError] = useState(null);

    //Current Loans
    const [shelfCurrentLoans, setShelfCurrentLoans] = useState<ShelfCurrentLoans[]>([]);
    const [isLoadingUserLoans, setIsLoadingUserLoans] = useState(true);

    useEffect(
        () => {
            const fetchUserCurrentLoans = async () => {
                if(authState && authState.isAuthenticated){
                    const url = `http://localhost:8080/api/books/secure/currentloans`;
                    const requestOptions = {
                        method: 'GET',
                        headers: {
                            Authorization: `Bearer ${authState.accessToken?.accessToken}`,
                            'Content-Type': 'application/json'
                        }
                    };
                    const shelfCurrentLoansResponse = await fetch(url, requestOptions);
                    if (!shelfCurrentLoansResponse.ok) {
                        throw new Error('Something went wrong!');
                    }
                    const currentLoansResponseJson = await shelfCurrentLoansResponse.json();
                    setShelfCurrentLoans(currentLoansResponseJson);
                }
                    setIsLoadingUserLoans(false);
                
            }
            fetchUserCurrentLoans().catch((error: any) =>{
            
            setIsLoadingUserLoans(false); 
            setHttpError(error)
        })
        window.scrollTo(0,0)
        }, [authState]);

        if (isLoadingUserLoans) {
            return (
                <SpinnerLoading/>
            )
        }

        if (httpError) {
            return (
                <div className="container m-5">
                    <p>
                        {httpError}
                    </p>
                </div>
            )
        }

    return();
}
