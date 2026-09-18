import Navbar from '../components/navbar/Navbar';
import PageHero from '../components/shared/PageHero';
import Templates from '../components/DocTemplate/Templates';

export default function LatexTemplates() {
    return (
        <>
            <div
                className="relative w-full overflow-hidden bg-top bg-no-repeat home-page-hero-bg border-b border-border"
                style={{ backgroundImage: "url('/assest/home_page.png')" }}
            >
                <Navbar />
                <PageHero
                    breadcrumb="LaTeX Templates"
                    title="Explore LaTeX Templates"
                    description="Browse and edit professional LaTeX templates — all free. Compile up to 20 times per day."
                />
            </div>
            <Templates variant="latex" />
        </>
    );
}

